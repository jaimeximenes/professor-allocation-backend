package com.project.professor.allocation.service;

import com.project.professor.allocation.entity.Course;
import com.project.professor.allocation.exception.ResourceNotFoundException;
import com.project.professor.allocation.repository.AllocationRepository;
import com.project.professor.allocation.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final AllocationRepository allocationRepository;

    public CourseService(CourseRepository courseRepository, AllocationRepository allocationRepository) {
        this.courseRepository = courseRepository;
        this.allocationRepository = allocationRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com o ID: " + id));
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, Course course) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado com o ID: " + id);
        }
        course.setId(id);
        return courseRepository.save(course);
    }

    public void deleteById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado com o ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    public List<Course> findByProfessorId(Long professorId) {
        return allocationRepository.findByProfessorId(professorId)
                .stream()
                .map(allocation -> allocation.getCourse())
                .distinct()
                .collect(Collectors.toList());
    }
}
