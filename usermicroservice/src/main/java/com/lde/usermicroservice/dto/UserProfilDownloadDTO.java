package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class UserProfilDownloadDTO {
    private UserProfilDTO user;
    private List<UserDownloadHistoryDTO> downloadHistory;
    private DownloadStatsDTO downloadStats;
}
