package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Department;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.DepartmentRepository;
import com.project.professor.allocation.repository.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void findById_returnsDepartment_whenExists() {
        Department department = new Department(1L, "Matemática");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThat(departmentService.findById(1L)).isSameAs(department);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_throwsResourceNotFoundException_whenMissing() {
        Department department = new Department(null, "Física");
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.update(1L, department))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteById_throwsResourceNotFoundException_whenMissing() {
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(departmentRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_deletes_whenExists() {
        when(departmentRepository.existsById(1L)).thenReturn(true);

        departmentService.deleteById(1L);

        verify(departmentRepository).deleteById(1L);
    }

    @Test
    void findByProfessorId_returnsProfessorDepartment() {
        Department department = new Department(1L, "Matemática");
        Professor professor = new Professor(10L, "João Silva", "11111111111", department);
        when(professorRepository.findById(10L)).thenReturn(Optional.of(professor));

        assertThat(departmentService.findByProfessorId(10L)).isSameAs(department);
    }

    @Test
    void findByProfessorId_throwsResourceNotFoundException_whenProfessorMissing() {
        when(professorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.findByProfessorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
