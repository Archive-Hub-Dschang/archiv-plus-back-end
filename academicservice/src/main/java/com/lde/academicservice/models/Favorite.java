package com.lde.academicservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "favorites")
public class Favorite {
    @Id
    private String id;
    private String user_id;
    private String subject_id;

}
