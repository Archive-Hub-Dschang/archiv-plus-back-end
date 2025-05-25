package com.lde.academicservice.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "exams")
public class Exam {
    @Id
    private String id;

    private String title;

    private ExamType type;

    private int year;

    private String pdfUrl;

    private String subjectId;

    @CreatedDate
    private LocalDate createdAt;
}
