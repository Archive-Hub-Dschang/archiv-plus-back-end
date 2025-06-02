package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.repositories.CorrectionRepository;
import com.lde.academicservice.services.CorrectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/corrections")
@RequiredArgsConstructor
public class CorrectionController {

    private final CorrectionService correctionService;


    @PostMapping("/{examId}/correction")
    @PreAuthorize("hasAuthority('Collaborateur') or hasAuthority('Admin')")
    public ResponseEntity<?> uploadCorrection(@PathVariable String examId, @RequestParam("pdf") MultipartFile pdf) {
        try {
            Correction correction = correctionService.addCorrection(examId, pdf);
            return ResponseEntity.ok(correction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Correction already exists for this exam");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading correction");
        }
    }

    @GetMapping("/{examId}/correction")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCorrection(@PathVariable String examId) {
        try {
            Correction correction = correctionService.getCorrectionByExamId(examId);
            return ResponseEntity.ok(correction);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
