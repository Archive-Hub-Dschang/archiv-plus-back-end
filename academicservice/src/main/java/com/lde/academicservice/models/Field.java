package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "fields")
public class Field {
    @Id
    private String id;
    private String name;

}