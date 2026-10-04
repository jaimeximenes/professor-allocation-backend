package com.project.professor.allocation.controller;

import com.project.professor.allocation.dto.AllocationDTO;
import com.project.professor.allocation.dto.ProfessorDTO;
import com.project.professor.allocation.entity.Allocation;
import com.project.professor.allocation.entity.Professor;
import com.project.professor.allocation.service.AllocationService;
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

@Tag(name = "Allocations", description = "Endpoints for managing allocations")
@RestController
@RequestMapping("/allocations")
public class AllocationController {

    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @Operation(summary = "Find all allocations")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Success")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AllocationDTO>> findAll() {
        List<AllocationDTO> dtos = allocationService.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Find an allocation by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Allocation not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AllocationDTO> findById(@PathVariable Long id) {
        Allocation allocation = allocationService.findById(id);
        return ResponseEntity.ok(convertToDTO(allocation));
    }

    @Operation(summary = "Find allocations by professor ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @GetMapping(value = "/professor/{professorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AllocationDTO>> findByProfessor(@PathVariable Long professorId) {
        List<AllocationDTO> dtos = allocationService.findByProfessorId(professorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Find the professor associated with a specific allocation ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Allocation not found")
    })
    @GetMapping(value = "/{id}/professor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfessorDTO> findProfessorByAllocation(@PathVariable Long id) {
        Allocation allocation = allocationService.findById(id);
        return ResponseEntity.ok(convertProfessorToDTO(allocation.getProfessor()));
    }

    @Operation(summary = "Create a new allocation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or scheduling collision"),
            @ApiResponse(responseCode = "404", description = "Professor or course not found")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AllocationDTO> create(@Valid @RequestBody AllocationDTO dto) {
        Allocation allocation = new Allocation(null, dto.getDayOfWeek(), dto.getStartHour(), dto.getEndHour(), null, null);
        Allocation savedAllocation = allocationService.save(allocation, dto.getProfessorId(), dto.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAllocation));
    }

    @Operation(summary = "Update an existing allocation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or scheduling collision"),
            @ApiResponse(responseCode = "404", description = "Allocation, professor or course not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AllocationDTO> update(@PathVariable Long id, @Valid @RequestBody AllocationDTO dto) {
        Allocation allocation = new Allocation(id, dto.getDayOfWeek(), dto.getStartHour(), dto.getEndHour(), null, null);
        Allocation updatedAllocation = allocationService.update(id, allocation, dto.getProfessorId(), dto.getCourseId());
        return ResponseEntity.ok(convertToDTO(updatedAllocation));
    }

    @Operation(summary = "Delete an allocation by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content (Deleted successfully)"),
            @ApiResponse(responseCode = "404", description = "Allocation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        allocationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ProfessorDTO convertProfessorToDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getName(),
                professor.getCpf(),
                professor.getDepartment() != null ? professor.getDepartment().getId() : null
        );
    }

    private AllocationDTO convertToDTO(Allocation allocation) {
        return new AllocationDTO(
                allocation.getId(),
                allocation.getDayOfWeek(),
                allocation.getStartHour(),
                allocation.getEndHour(),
                allocation.getProfessor() != null ? allocation.getProfessor().getId() : null,
                allocation.getCourse() != null ? allocation.getCourse().getId() : null
        );
    }
}
