package com.lde.usermicroservice.dto;

import lombok.Data;

@Data
public class DocumentDTO {
    private String id;
    private String name  ;
    private String filePath;
    private String correctionId;

}
