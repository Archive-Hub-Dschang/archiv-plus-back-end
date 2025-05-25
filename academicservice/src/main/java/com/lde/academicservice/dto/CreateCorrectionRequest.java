package com.lde.academicservice.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateCorrectionRequest(MultipartFile pdf) {
}