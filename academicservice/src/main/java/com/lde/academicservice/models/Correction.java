package com.lde.academicservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

    @Data
    @Builder
    @AllArgsConstructor
    @Document(collection = "corrections")
    public class Correction {
        @Id
        private String id;
        private String examId;
        private String pdfUrl;

        @CreatedDate
        private LocalDate createdAt;
    }
