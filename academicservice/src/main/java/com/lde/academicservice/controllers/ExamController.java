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
