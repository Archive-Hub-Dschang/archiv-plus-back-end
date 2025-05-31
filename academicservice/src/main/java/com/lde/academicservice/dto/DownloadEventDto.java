package com.lde.academicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadEventDto {
    private String id;
    private Long userId;
    private String examId;
    private String examTitle; // Le titre de l'examen est envoyé pour éviter un appel supplémentaire
    private LocalDateTime downloadDate;

}
