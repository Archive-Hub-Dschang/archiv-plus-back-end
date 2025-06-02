package com.lde.academicservice.services;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.models.*;
import com.lde.academicservice.repositories.*;
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

    public Exam createExam(CreateExamRequest request) throws IOException {
        MultipartFile file = request.pdf();

        // 1. Créer un nom de fichier unique
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 2. Déterminer un chemin absolu vers le dossier 'uploads' dans le répertoire du projet
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        Files.createDirectories(uploadPath); // Crée le dossier s’il n’existe pas

        // 3. Construire le chemin complet du fichier à enregistrer
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        // 4. Construire l’URL d’accès (pour un serveur configuré pour servir /uploads)
        String fileUrl = "/uploads/" + filename;

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

    public List<Exam> filterExamsFlexible(String departmentId, String programId, String levelId, String semesterId, String subjectId) {
        // Étape 1 : On part de toutes les matières
        List<Subject> subjects = subjectRepository.findAll();

        // On filtre selon semesterId si présent
        if (semesterId != null) {
            subjects = subjects.stream()
                    .filter(s -> s.getSemesterId().equals(semesterId))
                    .toList();
        }

        // Étape 2 : On récupère les semestres concernés
        Set<String> semesterIds = subjects.stream().map(Subject::getSemesterId).collect(Collectors.toSet());
        List<Semester> semesters = semesterRepository.findAllById(semesterIds);

        if (programId != null) {
            semesters = semesters.stream()
                    .filter(s -> s.getProgramId().equals(programId))
                    .toList();
        }

        if (levelId != null) {
            semesters = semesters.stream()
                    .filter(s -> s.getLevelId().equals(levelId))
                    .toList();
        }

        // Étape 3 : Vérifier si les semestres correspondent au programme du département
        if (departmentId != null) {
            Set<String> programIds = semesters.stream()
                    .map(Semester::getProgramId)
                    .collect(Collectors.toSet());
            List<Program> programs = programRepository.findAllById(programIds);
            Set<String> filteredPrograms = programs.stream()
                    .filter(p -> p.getDepartmentId().equals(departmentId))
                    .map(Program::getId)
                    .collect(Collectors.toSet());

            semesters = semesters.stream()
                    .filter(s -> filteredPrograms.contains(s.getProgramId()))
                    .toList();
        }

        // Étape 4 : On filtre les subjects selon les semestres restants
        Set<String> allowedSemesterIds = semesters.stream().map(Semester::getId).collect(Collectors.toSet());
        subjects = subjects.stream()
                .filter(s -> allowedSemesterIds.contains(s.getSemesterId()))
                .toList();

        if (subjectId != null) {
            subjects = subjects.stream()
                    .filter(s -> s.getId().equals(subjectId))
                    .toList();
        }

        // Étape 5 : Récupération des exams liés à ces subjects
        Set<String> subjectIds = subjects.stream().map(Subject::getId).collect(Collectors.toSet());
        return examRepository.findBySubjectIdIn(subjectIds);
    }

}
