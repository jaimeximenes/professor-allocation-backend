package com.project.professor.allocation.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldErrorDetails> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldErrorDetails {
        private String field;
        private String message;
    }
}
