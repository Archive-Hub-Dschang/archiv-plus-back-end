package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadStatsDTO {
    private long totalDownloads;           // Nombre total de téléchargements
    private long monthlyDownloads;         // Téléchargements ce mois
    private String mostDownloadedSubject;  // Matière la plus téléchargée (facultatif)
}
