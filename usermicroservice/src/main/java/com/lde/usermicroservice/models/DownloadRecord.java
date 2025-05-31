package com.lde.usermicroservice.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "download_records")
    public class DownloadRecord {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;

        @Column(name = "user_id", nullable = false)
        private Long userId;      // L'ID de l'utilisateur qui a téléchargé

        @Column(name = "exam_id", nullable = false)
        private String examId;      // L'ID de l'examen téléchargé (vient de AcademicService)

        @Column(name = "exam_title", nullable = false)
        private String examTitle;
        // Le titre de l'examen (vient de AcademicService, pour l'affichage)
        @Column(name = "exam_type", nullable = false)
        private String examType;
        @Column(name = "download_date", nullable = false)
        private LocalDateTime downloadDate; // Date et heure du téléchargement
    }


