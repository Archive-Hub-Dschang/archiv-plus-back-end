package com.lde.academicservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "semesters")
public class Semester {
    @Id
    private String id;
    private String label;
    private String programId;
    private String levelId;
}
