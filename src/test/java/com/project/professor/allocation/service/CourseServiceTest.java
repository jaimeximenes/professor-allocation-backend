package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Allocation;
import com.project.professor.allocation.entity.Course;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.AllocationRepository;
import com.project.professor.allocation.repository.CourseRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AllocationRepository allocationRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findById_returnsCourse_whenExists() {
        Course course = new Course(1L, "Cálculo I");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThat(courseService.findById(1L)).isSameAs(course);
    }

    @Test
    void findById_throwsResourceNotFoundException_whenMissing() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_throwsResourceNotFoundException_whenMissing() {
        Course course = new Course(null, "Cálculo II");
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.update(1L, course))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, never()).save(any());
    }

    @Test
    void update_setsIdAndSaves_whenExists() {
        Course course = new Course(null, "Cálculo II");
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.update(1L, course);

        assertThat(result.getId()).isEqualTo(1L);
        verify(courseRepository).save(course);
    }

    @Test
    void deleteById_throwsResourceNotFoundException_whenMissing() {
        when(courseRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> courseService.deleteById(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, never()).deleteById(any());
    }

    @Test
    void deleteById_deletes_whenExists() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        courseService.deleteById(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void findByProfessorId_returnsDistinctCoursesFromAllocations() {
        Course calculus = new Course(1L, "Cálculo I");
        Course physics = new Course(2L, "Física I");

        Allocation allocation1 = new Allocation(1L, null, null, null, null, calculus);
        Allocation allocation2 = new Allocation(2L, null, null, null, null, calculus);
        Allocation allocation3 = new Allocation(3L, null, null, null, null, physics);

        when(allocationRepository.findByProfessorId(10L))
                .thenReturn(List.of(allocation1, allocation2, allocation3));

        List<Course> result = courseService.findByProfessorId(10L);

        assertThat(result).containsExactlyInAnyOrder(calculus, physics);
    }
}
