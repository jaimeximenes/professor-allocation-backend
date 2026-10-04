package com.project.professor.allocation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {

    private Long id;

    @NotBlank(message = "O nome do professor é obrigatório")
    @Size(max = 100, message = "O nome do professor deve ter no máximo 100 caracteres")
    private String name;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;

    @NotNull(message = "O ID do departamento é obrigatório")
    private Long departmentId;
}
