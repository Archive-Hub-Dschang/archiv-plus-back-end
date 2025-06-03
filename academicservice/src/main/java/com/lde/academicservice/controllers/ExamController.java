package com.lde.academicservice.controllers;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.dto.ExamWithCorrectionDTO;
import com.lde.academicservice.models.Exam;
import com.lde.academicservice.models.ExamType;
import com.lde.academicservice.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/academics/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<Exam> uploadExam(
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam int year,
            @RequestParam String subjectId,
            @RequestParam("pdf") MultipartFile file
    ) throws IOException {
        CreateExamRequest request = new CreateExamRequest(title, type, year, subjectId, file);
        Exam savedExam = examService.createExam(request);
        return ResponseEntity.ok(savedExam);
    }

    @GetMapping("/with-corrections")
    public ResponseEntity<List<ExamWithCorrectionDTO>> getExamsWithCorrections(
            @RequestParam String departmentId,
            @RequestParam String programId,
            @RequestParam String levelId,
            @RequestParam String semesterId,
            @RequestParam String subjectId,
            @RequestParam ExamType type
    ) {
        return ResponseEntity.ok(
                examService.getExamsWithCorrections(
                        departmentId,
                        programId,
                        levelId,
                        semesterId,
                        subjectId,
                        type
                )
        );
    }
}
