package com.lde.academicservice.controllers;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.models.Exam;
import com.lde.academicservice.services.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestParam String title, @RequestParam MultipartFile pdf, @RequestParam int year, @RequestParam String subjectId, @RequestParam String type) {
        CreateExamRequest request = new CreateExamRequest(title, pdf, year, subjectId, type);
        try {
            Exam created = examService.createExam(request);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace(); // ou log l'erreur
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Exam>> filterExams(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String programId,
            @RequestParam(required = false) String levelId,
            @RequestParam(required = false) String semesterId,
            @RequestParam(required = false) String subjectId
    ) {
        List<Exam> exams = examService.filterExamsFlexible(departmentId, programId, levelId, semesterId, subjectId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
