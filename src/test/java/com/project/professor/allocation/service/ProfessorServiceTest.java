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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private ProfessorService professorService;

    @Test
    void findAll_returnsEverything_whenNameIsBlank() {
        when(professorRepository.findAll()).thenReturn(List.of());

        professorService.findAll("  ");

        verify(professorRepository).findAll();
        verify(professorRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    void findAll_filtersByName_whenNameIsProvided() {
        when(professorRepository.findByNameContainingIgnoreCase("joão")).thenReturn(List.of());

        professorService.findAll("joão");

        verify(professorRepository).findByNameContainingIgnoreCase("joão");
        verify(professorRepository, never()).findAll();
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(professorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void save_throwsResourceNotFoundException_whenDepartmentMissing() {
        Professor professor = new Professor(null, "João Silva", "11111111111", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.save(professor, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(professorRepository, never()).save(any());
    }

    @Test
    void save_setsDepartmentAndSaves_whenDepartmentExists() {
        Department department = new Department(1L, "Matemática");
        Professor professor = new Professor(null, "João Silva", "11111111111", null);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(professorRepository.save(professor)).thenReturn(professor);

        Professor result = professorService.save(professor, 1L);

        assertThat(result.getDepartment()).isSameAs(department);
        verify(professorRepository).save(professor);
    }

    @Test
    void update_throwsResourceNotFoundException_whenProfessorMissing() {
        Professor professor = new Professor(null, "João Silva", "11111111111", null);
        when(professorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> professorService.update(1L, professor, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(professorRepository, never()).save(any());
    }

    @Test
    void update_throwsResourceNotFoundException_whenDepartmentMissing() {
        Professor professor = new Professor(null, "João Silva", "11111111111", null);
        when(professorRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> professorService.update(1L, professor, 2L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(professorRepository, never()).save(any());
    }

    @Test
    void update_setsIdAndDepartmentAndSaves_whenBothExist() {
        Department department = new Department(2L, "Física");
        Professor professor = new Professor(null, "João Silva", "11111111111", null);
        when(professorRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department));
        when(professorRepository.save(professor)).thenReturn(professor);

        Professor result = professorService.update(1L, professor, 2L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDepartment()).isSameAs(department);
        verify(professorRepository).save(professor);
    }

    @Test
    void deleteById_throwsResourceNotFoundException_whenMissing() {
        when(professorRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> professorService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(professorRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_deletes_whenExists() {
        when(professorRepository.existsById(1L)).thenReturn(true);

        professorService.deleteById(1L);

        verify(professorRepository).deleteById(1L);
    }
}
