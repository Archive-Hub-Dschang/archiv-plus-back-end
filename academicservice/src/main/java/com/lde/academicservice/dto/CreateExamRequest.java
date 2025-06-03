package com.lde.academicservice.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateExamRequest(
        String title,
        String type,
        int year,
        String subjectId,
        MultipartFile pdf
) {}
