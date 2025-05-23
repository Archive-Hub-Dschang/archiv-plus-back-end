package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "subjects")
public class Document {
    @Id
    private String id;
    private String name;
    private String file_path;
    private String id_level;
    private String id_field;
    private String id_department;
    private String correction_id;
}
