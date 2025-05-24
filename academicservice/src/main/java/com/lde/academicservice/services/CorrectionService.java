package com.lde.academicservice.services;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.repositories.CorrectionRepository;
import com.lde.academicservice.repositories.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CorrectionService {

    private final CorrectionRepository correctionRepository;
    private final ExamRepository examRepository;

    public Correction addCorrection(String examId, MultipartFile pdf) throws IOException {
        if (!examRepository.existsById(examId)) {
            throw new IllegalArgumentException("Exam not found");
        }

        if (correctionRepository.existsByExamId(examId)) {
            throw new IllegalStateException("Correction already exists for this exam");
        }

        String filename = UUID.randomUUID() + "_" + pdf.getOriginalFilename();
        File uploadDir = new File("uploads/corrections");
        if (!uploadDir.exists()) uploadDir.mkdirs();

        File dest = new File(uploadDir, filename);
        pdf.transferTo(dest);

        String fileUrl = "/uploads/corrections/" + filename;

        Correction correction = Correction.builder().examId(examId).pdfUrl(fileUrl).createdAt(LocalDate.now()).build();

        return correctionRepository.save(correction);
    }

    public Correction getCorrectionByExamId(String examId) {
        return correctionRepository.findByExamId(examId).orElseThrow(() -> new NoSuchElementException("No correction found for this exam"));
    }
}
