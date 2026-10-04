package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Allocation;
import com.project.professor.allocation.entity.Course;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.AllocationRepository;
import com.project.professor.allocation.repository.CourseRepository;
import com.project.professor.allocation.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllocationService {

	private final AllocationRepository allocationRepository;
	private final ProfessorRepository professorRepository;
	private final CourseRepository courseRepository;

	public AllocationService(AllocationRepository allocationRepository, ProfessorRepository professorRepository, CourseRepository courseRepository) {
		this.allocationRepository = allocationRepository;
		this.professorRepository = professorRepository;
		this.courseRepository = courseRepository;
	}

	public List<Allocation> findAll() {
		return allocationRepository.findAll();
	}

	public Allocation findById(Long id) {
		return allocationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Alocação não encontrada com o ID: " + id));
	}

	public List<Allocation> findByProfessorId(Long professorId) {
		return allocationRepository.findByProfessorId(professorId);
	}

	public Allocation save(Allocation allocation, Long professorId, Long courseId) {
		validateHours(allocation);
		Professor professor = professorRepository.findById(professorId)
				.orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com o ID: " + professorId));
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + courseId));

		allocation.setProfessor(professor);
		allocation.setCourse(course);

		if (hasCollision(allocation)) {
			throw new IllegalArgumentException("O professor já possui uma alocação nesse horário.");
		}

		return allocationRepository.save(allocation);
	}

	public Allocation update(Long id, Allocation allocation, Long professorId, Long courseId) {
		if (!allocationRepository.existsById(id)) {
			throw new ResourceNotFoundException("Alocação não encontrada com o ID: " + id);
		}
		validateHours(allocation);
		Professor professor = professorRepository.findById(professorId)
				.orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com o ID: " + professorId));
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + courseId));

		allocation.setId(id);
		allocation.setProfessor(professor);
		allocation.setCourse(course);

		if (hasCollision(allocation)) {
			throw new IllegalArgumentException("O professor já possui uma alocação nesse horário.");
		}

		return allocationRepository.save(allocation);
	}

	public void deleteById(Long id) {
		if (!allocationRepository.existsById(id)) {
			throw new ResourceNotFoundException("Alocação não encontrada com o ID: " + id);
		}
		allocationRepository.deleteById(id);
	}

	private void validateHours(Allocation allocation) {
		if (allocation.getStartHour().isAfter(allocation.getEndHour()) || allocation.getStartHour().equals(allocation.getEndHour())) {
			throw new IllegalArgumentException("A hora de início deve ser anterior à hora de término.");
		}
	}

	private boolean hasCollision(Allocation allocation) {
		List<Allocation> allocations = allocationRepository.findByProfessorId(allocation.getProfessor().getId());

		return allocations.stream()
				.filter(existing -> allocation.getId() == null || !existing.getId().equals(allocation.getId()))
				.anyMatch(existing -> hasCollision(existing, allocation));
	}

	private boolean hasCollision(Allocation currentAllocation, Allocation newAllocation) {
		return currentAllocation.getDayOfWeek() == newAllocation.getDayOfWeek()
				&& newAllocation.getStartHour().isBefore(currentAllocation.getEndHour())
				&& currentAllocation.getStartHour().isBefore(newAllocation.getEndHour());
	}
}
