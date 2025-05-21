package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "levels")
public class Level {
    @Id
    private String id;
    private String name;


}
