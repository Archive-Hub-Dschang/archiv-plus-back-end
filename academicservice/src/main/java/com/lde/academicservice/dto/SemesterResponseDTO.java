package com.lde.academicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@AllArgsConstructor
@Data
public class SemesterResponseDTO {
    private String id;
    private LocalDate endSemesterDate;
}
