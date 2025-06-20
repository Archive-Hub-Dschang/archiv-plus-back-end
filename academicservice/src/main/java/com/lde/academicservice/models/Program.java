package com.lde.academicservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "programs")
@Data
@AllArgsConstructor
public class Program {
    @Id
    private String id;
    private String name;
    private String departmentId;
}