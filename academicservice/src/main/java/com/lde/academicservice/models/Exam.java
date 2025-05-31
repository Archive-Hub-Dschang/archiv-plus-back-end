package com.lde.academicservice.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

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
    private int downloadCount;
    @CreatedDate
    private LocalDate createdAt;

    public boolean isPopular() {
        return downloadCount >= 100; // Consider exam popular if downloaded 100+ times
    }

    public boolean isRecent() {
        return createdAt.isAfter(ChronoLocalDate.from(LocalDateTime.now().minusDays(7))); // Recent if uploaded within last 7 days
    }
}
