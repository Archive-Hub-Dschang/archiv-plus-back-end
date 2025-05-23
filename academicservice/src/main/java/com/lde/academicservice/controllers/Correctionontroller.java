package com.lde.academicservice.controllers;

import com.lde.academicservice.services.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/corrections")
@RequiredArgsConstructor
public class CorrectionController {

    private final CorrectionService correctionService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam String departmentId,
                                         @RequestParam String subjectId,
                                         @RequestParam String fieldId) throws IOException {
        String id = correctionService.uploadCorrection(file, departmentId, subjectId, fieldId);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        correctionService.downloadCorrection(id, response);
    }
}