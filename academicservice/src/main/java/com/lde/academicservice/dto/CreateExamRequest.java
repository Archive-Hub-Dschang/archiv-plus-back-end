package com.lde.academicservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateExamRequest(
        @NotBlank String title,
        @NotNull MultipartFile pdf,
        @NotNull int year,
        @NotBlank String subjectId,
        @NotNull String type // Doit être "CC" ou "EXAM"
) {
}