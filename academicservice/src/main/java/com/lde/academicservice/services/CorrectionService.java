package com.lde.academicservice.services;

import com.lde.academicservice.models.Correction;
import com.lde.academicservice.repositories.CorrectionRepository;
import com.lde.academicservice.repositories.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        // 1. Nom unique pour le fichier
        String filename = UUID.randomUUID() + "_" + pdf.getOriginalFilename();

        // 2. Dossier absolu "uploads/corrections"
        Path correctionPath = Paths.get(System.getProperty("user.dir"), "uploads", "corrections");
        Files.createDirectories(correctionPath); // Crée le dossier si nécessaire

        // 3. Enregistrement du fichier
        Path filePath = correctionPath.resolve(filename);
        pdf.transferTo(filePath.toFile());

        // 4. URL publique
        String fileUrl = "/uploads/corrections/" + filename;

        // 5. Création et sauvegarde
        Correction correction = Correction.builder()
                .examId(examId)
                .pdfUrl(fileUrl)
                .createdAt(LocalDate.now())
                .build();

        return correctionRepository.save(correction);
    }

    public Correction getCorrectionByExamId(String examId) {
        return correctionRepository.findByExamId(examId).orElseThrow(() -> new NoSuchElementException("No correction found for this exam"));
    }
}
