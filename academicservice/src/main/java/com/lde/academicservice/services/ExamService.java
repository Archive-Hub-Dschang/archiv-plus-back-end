package com.lde.academicservice.services;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.dto.ExamWithCorrectionDTO;
import com.lde.academicservice.models.*;
import com.lde.academicservice.repositories.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final CorrectionRepository correctionRepository;



    public Exam createExam(CreateExamRequest request) throws IOException {
        MultipartFile file = request.pdf();

        // 1. Créer un nom de fichier unique
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. Déterminer un chemin absolu vers le dossier 'uploads' dans le répertoire du projet
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "exams");
        Files.createDirectories(uploadPath); // Crée le dossier s’il n’existe pas

        // 3. Construire le chemin complet du fichier à enregistrer
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 4. Construire l’URL d’accès (pour un serveur configuré pour servir /uploads)
        String fileUrl = "/uploads/exams/" + filename;

        // 5. Construire et enregistrer l’examen
        Exam exam = Exam.builder()
                .title(request.title())
                .type(ExamType.valueOf(request.type().toUpperCase()))
                .year(request.year())
                .pdfUrl(fileUrl)
                .subjectId(request.subjectId())
                .createdAt(LocalDate.now())
                .build();

        return examRepository.save(exam);
    }

    public List<ExamWithCorrectionDTO> getExamsWithCorrections(
            String departmentId,
            String programId,
            String levelId,
            String semesterId,
            String subjectId,
            ExamType examType // CC ou EXAM
    ) {
        // Vérification des liens hiérarchiques
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

        if (!subject.getSemesterId().equals(semesterId)) {
            throw new RuntimeException("La matière ne correspond pas au semestre");
        }

        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));

        if (!semester.getLevelId().equals(levelId)) {
            throw new RuntimeException("Le niveau ne correspond pas au semestre");
        }

        if (!semester.getProgramId().equals(programId)) {
            throw new RuntimeException("Le programme ne correspond pas au semestre");
        }

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programme non trouvé"));

        if (!program.getDepartmentId().equals(departmentId)) {
            throw new RuntimeException("Le département ne correspond pas au programme");
        }

        // Récupération des épreuves
        List<Exam> exams = examRepository.findBySubjectIdAndType(subjectId, examType);

        // Récupération des corrections
        return exams.stream().map(exam -> {
            Optional<Correction> correction = correctionRepository.findByExamId(exam.getId());
            return new ExamWithCorrectionDTO(exam, correction.orElse(null));
        }).toList();
    }
}
