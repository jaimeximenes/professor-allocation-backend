package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Allocation;
import com.project.professor.allocation.entity.Course;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.AllocationRepository;
import com.project.professor.allocation.repository.CourseRepository;
import com.project.professor.allocation.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AllocationService allocationService;

    private Professor professor;
    private Course course;

    @BeforeEach
    void setUp() {
        professor = new Professor(1L, "João Silva", "11111111111", null);
        course = new Course(1L, "Cálculo I");
    }

    private Allocation newAllocation(DayOfWeek day, LocalTime start, LocalTime end) {
        Allocation allocation = new Allocation();
        allocation.setDayOfWeek(day);
        allocation.setStartHour(start);
        allocation.setEndHour(end);
        return allocation;
    }

    @Test
    void findById_returnsAllocation_whenExists() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        Allocation result = allocationService.findById(1L);

        assertThat(result).isSameAs(allocation);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(allocationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> allocationService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void save_throwsIllegalArgumentException_whenStartHourIsAfterEndHour() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(8, 0));

        assertThatThrownBy(() -> allocationService.save(allocation, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void save_throwsIllegalArgumentException_whenStartHourEqualsEndHour() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(8, 0));

        assertThatThrownBy(() -> allocationService.save(allocation, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void save_throwsResourceNotFoundException_whenProfessorDoesNotExist() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        when(professorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> allocationService.save(allocation, 1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void save_throwsResourceNotFoundException_whenCourseDoesNotExist() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> allocationService.save(allocation, 1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void save_throwsIllegalArgumentException_whenProfessorHasOverlappingAllocation() {
        Allocation newAllocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));
        Allocation existingAllocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(allocationRepository.findByProfessorId(1L)).thenReturn(List.of(existingAllocation));

        assertThatThrownBy(() -> allocationService.save(newAllocation, 1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void save_succeeds_whenSameDayButNonOverlapping() {
        Allocation newAllocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        Allocation existingAllocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(allocationRepository.findByProfessorId(1L)).thenReturn(List.of(existingAllocation));
        when(allocationRepository.save(newAllocation)).thenReturn(newAllocation);

        Allocation result = allocationService.save(newAllocation, 1L, 1L);

        assertThat(result.getProfessor()).isSameAs(professor);
        assertThat(result.getCourse()).isSameAs(course);
        verify(allocationRepository).save(newAllocation);
    }

    @Test
    void save_succeeds_whenDifferentDay() {
        Allocation newAllocation = newAllocation(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        Allocation existingAllocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(allocationRepository.findByProfessorId(1L)).thenReturn(List.of(existingAllocation));
        when(allocationRepository.save(newAllocation)).thenReturn(newAllocation);

        Allocation result = allocationService.save(newAllocation, 1L, 1L);

        assertThat(result).isSameAs(newAllocation);
        verify(allocationRepository).save(newAllocation);
    }

    @Test
    void update_throwsResourceNotFoundException_whenAllocationDoesNotExist() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        when(allocationRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> allocationService.update(1L, allocation, 1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(allocationRepository, never()).save(any());
    }

    @Test
    void update_ignoresCollisionAgainstItself() {
        Allocation allocation = newAllocation(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        allocation.setId(5L);

        when(allocationRepository.existsById(5L)).thenReturn(true);
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(allocationRepository.findByProfessorId(1L)).thenReturn(List.of(allocation));
        when(allocationRepository.save(allocation)).thenReturn(allocation);

        Allocation result = allocationService.update(5L, allocation, 1L, 1L);

        assertThat(result.getId()).isEqualTo(5L);
        verify(allocationRepository).save(allocation);
    }

    @Test
    void deleteById_throwsResourceNotFoundException_whenMissing() {
        when(allocationRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> allocationService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(allocationRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_deletes_whenExists() {
        when(allocationRepository.existsById(1L)).thenReturn(true);

        allocationService.deleteById(1L);

        verify(allocationRepository).deleteById(1L);
    }
}
