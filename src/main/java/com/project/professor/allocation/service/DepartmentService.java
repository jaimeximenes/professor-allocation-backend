package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Department;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.DepartmentRepository;
import com.project.professor.allocation.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final ProfessorRepository professorRepository;

	public DepartmentService(DepartmentRepository departmentRepository, ProfessorRepository professorRepository) {
		this.departmentRepository = departmentRepository;
		this.professorRepository = professorRepository;
	}

	public List<Department> findAll() {
		return departmentRepository.findAll();
	}

	public Department findById(Long id) {
		return departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado com o ID: " + id));
	}

	public Department save(Department department) {
		return departmentRepository.save(department);
	}

	public Department update(Long id, Department department) {
		if (!departmentRepository.existsById(id)) {
			throw new ResourceNotFoundException("Departamento não encontrado com o ID: " + id);
		}
		department.setId(id);
		return departmentRepository.save(department);
	}

	public void deleteById(Long id) {
		if (!departmentRepository.existsById(id)) {
			throw new ResourceNotFoundException("Departamento não encontrado com o ID: " + id);
		}
		departmentRepository.deleteById(id);
	}

	public Department findByProfessorId(Long professorId) {
		Professor professor = professorRepository.findById(professorId)
				.orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com o ID: " + professorId));
		return professor.getDepartment();
	}
}
