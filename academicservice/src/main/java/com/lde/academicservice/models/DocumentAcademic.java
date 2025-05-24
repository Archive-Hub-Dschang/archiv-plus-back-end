package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class DocumentAcademic {
    @Id
    private String id;

    @Indexed
    private String fileName;

    private String contentType;
    private long fileSize;

    @Indexed
    private LocalDateTime uploadDate;

    @Indexed
    private String author;

    private String gridFsId;

    @Indexed
    private String levelId;

    @Indexed
    private String departmentId;

    @Indexed
    private String subjectId;

    @Indexed
    private String fieldId;

    @Indexed
    private int downloadCount;

    private LocalDateTime lastDownloadDate;

    private String description;

    private String[] tags;

    private boolean isPublic;

    private boolean isHidden;

    private String hiddenReason;

    private LocalDateTime hiddenDate;

    private String hiddenBy;

    private String version;

    private LocalDateTime lastModifiedDate;

    private String modifiedBy;

    public DocumentAcademic() {
        this.uploadDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.downloadCount = 0;
        this.isPublic = true;
        this.isHidden = false;
        this.version = "1.0";
    }


    public void incrementDownloadCount() {
        this.downloadCount++;
        this.lastDownloadDate = LocalDateTime.now();
    }

    public void updateModificationInfo(String modifiedBy) {
        this.lastModifiedDate = LocalDateTime.now();
        this.modifiedBy = modifiedBy;
    }

    public void hide(String reason, String hiddenBy) {
        this.isHidden = true;
        this.hiddenReason = reason;
        this.hiddenDate = LocalDateTime.now();
        this.hiddenBy = hiddenBy;
        this.updateModificationInfo(hiddenBy);
    }

    public void unhide(String modifiedBy) {
        this.isHidden = false;
        this.hiddenReason = null;
        this.hiddenDate = null;
        this.hiddenBy = null;
        this.updateModificationInfo(modifiedBy);
    }

    // Helper methods
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public boolean isPopular() {
        return downloadCount >= 100; // Consider document popular if downloaded 100+ times
    }

    public boolean isRecent() {
        return uploadDate.isAfter(LocalDateTime.now().minusDays(7)); // Recent if uploaded within last 7 days
    }
}