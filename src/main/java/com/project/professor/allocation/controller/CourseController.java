package com.project.professor.allocation.controller;

import com.project.professor.allocation.dto.CourseDTO;
import com.project.professor.allocation.entity.Course;
import com.project.professor.allocation.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Courses", description = "Endpoints for managing courses")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Find all courses")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CourseDTO>> findAll() {
        List<CourseDTO> dtos = courseService.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Find a course by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> findById(@PathVariable Long id) {
        Course course = courseService.findById(id);
        return ResponseEntity.ok(convertToDTO(course));
    }

    @Operation(summary = "Find courses by professor ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(value = "/professor/{professorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CourseDTO>> findByProfessor(@PathVariable Long professorId) {
        List<CourseDTO> dtos = courseService.findByProfessorId(professorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Create a new course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> create(@Valid @RequestBody CourseDTO dto) {
        Course course = convertToEntity(dto);
        Course savedCourse = courseService.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedCourse));
    }

    @Operation(summary = "Update an existing course")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        Course course = convertToEntity(dto);
        Course updatedCourse = courseService.update(id, course);
        return ResponseEntity.ok(convertToDTO(updatedCourse));
    }

    @Operation(summary = "Delete a course by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content (Deleted successfully)"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CourseDTO convertToDTO(Course course) {
        return new CourseDTO(course.getId(), course.getName());
    }

    private Course convertToEntity(CourseDTO dto) {
        return new Course(dto.getId(), dto.getName());
    }
}
