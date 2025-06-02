package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class UserDownloadHistoryDTO {
    private String recordId;
    private String examId;
    private String examTitle;
    private LocalDateTime downloadDate;
    private String examType;
}
