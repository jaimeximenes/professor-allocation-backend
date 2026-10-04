package com.project.professor.allocation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private Long id;

    @NotBlank(message = "O nome do departamento é obrigatório")
    @Size(max = 100, message = "O nome do departamento deve ter no máximo 100 caracteres")
    private String name;
}
