package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "corrections")
public class Correction {
    @Id
    private String id;
    private String doc_id;
    private String file_path;
    private String name;
}
