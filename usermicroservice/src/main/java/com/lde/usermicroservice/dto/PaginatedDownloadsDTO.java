package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PaginatedDownloadsDTO {
    private List<UserDownloadHistoryDTO> downloads;
    private PaginationDTO pagination;
}
