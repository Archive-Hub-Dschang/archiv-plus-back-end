package com.lde.paymentmicroservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SemesterDTO {
    private String id;
    private LocalDate endSemesterDate;
}
