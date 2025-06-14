package com.lde.academicservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "departments")
public class Department {
    @Id
    private String id;
    private String name;
}
