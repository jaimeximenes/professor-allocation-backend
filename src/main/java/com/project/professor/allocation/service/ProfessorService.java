package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Department;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.DepartmentRepository;
import com.project.professor.allocation.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;

    public ProfessorService(ProfessorRepository professorRepository, DepartmentRepository departmentRepository) {
        this.professorRepository = professorRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Professor> findAll(String name) {
        if (name != null && !name.isBlank()) {
            return professorRepository.findByNameContainingIgnoreCase(name);
        }
        return professorRepository.findAll();
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com o ID: " + id));
    }

    public Professor save(Professor professor, Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado com o ID: " + departmentId));
        professor.setDepartment(department);
        return professorRepository.save(professor);
    }

    public Professor update(Long id, Professor professor, Long departmentId) {
        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Professor não encontrado com o ID: " + id);
        }
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado com o ID: " + departmentId));
        professor.setId(id);
        professor.setDepartment(department);
        return professorRepository.save(professor);
    }

    public void deleteById(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Professor não encontrado com o ID: " + id);
        }
        professorRepository.deleteById(id);
    }
}
