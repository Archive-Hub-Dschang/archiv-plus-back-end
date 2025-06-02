package com.lde.usermicroservice.dto;

import lombok.Data;

@Data
public class ExamDTO {
    private String id;
    private String title  ;
    private String pdfUrl;
    private String correctionId;

}
