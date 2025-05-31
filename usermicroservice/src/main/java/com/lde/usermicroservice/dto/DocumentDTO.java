package com.lde.usermicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private String id;
    private String title;
    private String type;
    private String description;
    private String subject;
    private String level;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String createdBy;
    private Boolean isActive;
    private Integer downloadCount;

    // Constructeurs pour compatibilité avec différentes versions du service
    public DocumentDTO(String id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }
}