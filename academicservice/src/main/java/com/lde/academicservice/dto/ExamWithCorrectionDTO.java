package com.lde.academicservice.dto;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.models.Exam;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExamWithCorrectionDTO {
    private Exam exam;
    private Correction correction;
}