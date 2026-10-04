package com.project.professor.allocation.controller;

import com.project.professor.allocation.dto.DepartmentDTO;
import com.project.professor.allocation.entity.Department;
import com.project.professor.allocation.service.DepartmentService;
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

@Tag(name = "Departments", description = "Endpoints for managing departments")
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "Find all departments")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DepartmentDTO>> findAll() {
        List<DepartmentDTO> dtos = departmentService.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Find a department by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentDTO> findById(@PathVariable Long id) {
        Department department = departmentService.findById(id);
        return ResponseEntity.ok(convertToDTO(department));
    }

    @Operation(summary = "Find department by professor ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Professor or department not found")
    })
    @GetMapping(value = "/professor/{professorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentDTO> findByProfessor(@PathVariable Long professorId) {
        Department department = departmentService.findByProfessorId(professorId);
        return ResponseEntity.ok(convertToDTO(department));
    }

    @Operation(summary = "Create a new department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentDTO> create(@Valid @RequestBody DepartmentDTO dto) {
        Department department = convertToEntity(dto);
        Department savedDepartment = departmentService.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedDepartment));
    }

    @Operation(summary = "Update an existing department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentDTO> update(@PathVariable Long id, @Valid @RequestBody DepartmentDTO dto) {
        Department department = convertToEntity(dto);
        Department updatedDepartment = departmentService.update(id, department);
        return ResponseEntity.ok(convertToDTO(updatedDepartment));
    }

    @Operation(summary = "Delete a department by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content (Deleted successfully)"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DepartmentDTO convertToDTO(Department department) {
        return new DepartmentDTO(department.getId(), department.getName());
    }

    private Department convertToEntity(DepartmentDTO dto) {
        return new Department(dto.getId(), dto.getName());
    }
}
