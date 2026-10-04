package com.project.professor.allocation.controller;

import com.project.professor.allocation.dto.ProfessorDTO;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.service.ProfessorService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Professors", description = "Endpoints for managing professors")
@RestController
@RequestMapping("/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @Operation(summary = "Find all professors, optionally filtering by name")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProfessorDTO>> findAll(@RequestParam(required = false) String name) {
        List<ProfessorDTO> dtos = professorService.findAll(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Find a professor by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Professor not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfessorDTO> findById(@PathVariable Long id) {
        Professor professor = professorService.findById(id);
        return ResponseEntity.ok(convertToDTO(professor));
    }

    @Operation(summary = "Create a new professor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfessorDTO> create(@Valid @RequestBody ProfessorDTO dto) {
        Professor professor = new Professor(null, dto.getName(), dto.getCpf(), null);
        Professor savedProfessor = professorService.save(professor, dto.getDepartmentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedProfessor));
    }

    @Operation(summary = "Update an existing professor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Professor or department not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfessorDTO> update(@PathVariable Long id, @Valid @RequestBody ProfessorDTO dto) {
        Professor professor = new Professor(id, dto.getName(), dto.getCpf(), null);
        Professor updatedProfessor = professorService.update(id, professor, dto.getDepartmentId());
        return ResponseEntity.ok(convertToDTO(updatedProfessor));
    }

    @Operation(summary = "Delete a professor by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content (Deleted successfully)"),
            @ApiResponse(responseCode = "404", description = "Professor not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProfessorDTO convertToDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getName(),
                professor.getCpf(),
                professor.getDepartment() != null ? professor.getDepartment().getId() : null
        );
    }
}
