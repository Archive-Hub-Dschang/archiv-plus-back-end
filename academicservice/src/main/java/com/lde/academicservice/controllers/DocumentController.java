package com.lde.academicservice.controllers;

import com.lde.academicservice.services.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam String departmentId,
                                         @RequestParam String subjectId,
                                         @RequestParam String fieldId) throws IOException {
        String id = documentService.uploadDocument(file, departmentId, subjectId, fieldId);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
        documentService.downloadDocument(id, response);
    }
}