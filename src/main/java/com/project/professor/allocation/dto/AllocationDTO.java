package com.project.professor.allocation.dto;

import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocationDTO {

    private Long id;

    @NotNull(message = "O dia da semana é obrigatório")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "A hora de início é obrigatória")
    private LocalTime startHour;

    @NotNull(message = "A hora de término é obrigatória")
    private LocalTime endHour;

    @NotNull(message = "O ID do professor é obrigatório")
    private Long professorId;

    @NotNull(message = "O ID do curso é obrigatório")
    private Long courseId;
}
