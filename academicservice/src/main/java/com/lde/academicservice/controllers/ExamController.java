package com.lde.academicservice.controllers;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.models.Exam;
import com.lde.academicservice.services.ExamService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestParam String title, @RequestParam MultipartFile pdf, @RequestParam int year, @RequestParam String subjectId, @RequestParam String type,@RequestParam int downloadCount) {
        CreateExamRequest request = new CreateExamRequest(title, pdf, year, subjectId, type,downloadCount);
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
    // READ - Get all exams with pagination
    @GetMapping
    public ResponseEntity<Page<Exam>> getAllExams(Pageable pageable) {
        Page<Exam> exams = examService.getAllExams(pageable);
        return ResponseEntity.ok(exams);
    }

    // READ - Get exam by ID
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable String id) {
        Optional<Exam> exam = examService.getExamById(id);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // READ - Get most downloaded exams
    @GetMapping("/popular")
    public ResponseEntity<List<Exam>> getMostDownloadedExams(@RequestParam(defaultValue = "10") int limit) {
        List<Exam> exams = examService.getMostDownloadedExams(limit);
        return ResponseEntity.ok(exams);
    }
    // READ - Get most downloaded exams
    @GetMapping("/recent")
    public ResponseEntity<List<Exam>> getRecentDownloadedExams(@RequestParam(defaultValue = "10") int limit) {
        List<Exam> exams = examService.getRecentDownloadedExams(limit);
        return ResponseEntity.ok(exams);
    }


    // UPDATE - Update exam
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable String id,
                                           @RequestBody Exam examUpdate) {
        try {
            Exam updatedExam = examService.updateExam(id, examUpdate);
            return ResponseEntity.ok(updatedExam);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Delete exam
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable String id) {
        try {
            examService.deleteExam(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DOWNLOAD
    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        examService.downloadExam(id, response);
    }


    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
