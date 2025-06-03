package com.lde.academicservice.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class SemesterDTO {
    private String id;
    private LocalDate endSemesterDate;
}
