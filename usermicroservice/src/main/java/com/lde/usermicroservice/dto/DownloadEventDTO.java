package com.lde.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadEventDTO {
    private Long userId;
    private String examId;
    private String examTitle;
    private String examType;// Le titre de l'examen est envoyé pour éviter un appel supplémentaire
    private LocalDateTime downloadDate;


}
