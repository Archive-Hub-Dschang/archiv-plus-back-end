package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.services.CorrectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/academics/corrections")
@RequiredArgsConstructor
public class CorrectionController {

    private final CorrectionService correctionService;

    @PostMapping("/{examId}")
    public ResponseEntity<Correction> uploadCorrection(
            @PathVariable String examId,
            @RequestParam("pdf") MultipartFile pdf
    ) throws IOException {
        Correction savedCorrection = correctionService.addCorrection(examId, pdf);
        return ResponseEntity.ok(savedCorrection);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Correction> getCorrection(@PathVariable String examId) {
        Correction correction = correctionService.getCorrectionByExamId(examId);
        return ResponseEntity.ok(correction);
    }



}
