package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {
    @Id
    private String id;

    private String fileName;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadDate;

    private String gridFsId;

    private String departmentId;
    private String subjectId;
    private String fieldId;

}
