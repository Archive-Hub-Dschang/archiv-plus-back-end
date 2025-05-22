package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "subjects")
public class Subject {
    @Id
    private String id;
    private String name;
    private String file_path;
    private String correction_id;

    public Subject(String id, String correction_id, String file_path, String name) {
        this.id = id;
        this.correction_id = correction_id;
        this.file_path = file_path;
        this.name = name;
    }

    public Subject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorrection_id() {
        return correction_id;
    }

    public void setCorrection_id(String correction_id) {
        this.correction_id = correction_id;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
