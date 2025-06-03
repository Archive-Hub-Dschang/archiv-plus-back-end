package com.lde.academicservice.services;

import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.models.*;
import com.lde.academicservice.repositories.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private final MongoTemplate mongoTemplate;

    // Méthode utilitaire pour reconstruire le chemin physique du fichier
    private Path getPhysicalFilePath(String pdfUrl) {
        if (pdfUrl == null || !pdfUrl.startsWith("/uploads/")) {
            // Gérer les cas où l'URL n'est pas valide ou ne correspond pas au format attendu
            throw new IllegalArgumentException("Format d'URL PDF invalide: " + pdfUrl);
        }
        // Extraire le nom de fichier unique de l'URL (ex: /uploads/UUID_original.pdf -> UUID_original.pdf)
        String uniqueFilename = pdfUrl.substring("/uploads/".length());
        // Reconstruire le chemin physique absolu sur le système de fichiers
        return Paths.get(System.getProperty("user.dir"), "uploads", uniqueFilename);
    }

    // Méthode utilitaire pour extraire le nom de fichier original (après l'UUID)
    private String extractOriginalFileName(String uniqueFilename) {
        // Cherche le premier underscore après l'UUID
        int underscoreIndex = uniqueFilename.indexOf('_');
        if (underscoreIndex != -1 && underscoreIndex < uniqueFilename.length() - 1) {
            // Retourne la partie après l'underscore
            return uniqueFilename.substring(underscoreIndex + 1);
        }
        // Si aucun underscore n'est trouvé ou si le format est inattendu, retourne le nom unique
        return uniqueFilename;
    }

    public Exam createExam(CreateExamRequest request) throws IOException {
        MultipartFile file = request.pdf();

        String originalFilename = file.getOriginalFilename();
        // Crée un nom de fichier unique en préfixant avec un UUID
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        // Détermine le chemin absolu du dossier 'uploads' dans le répertoire du projet
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
        // Crée le dossier s'il n'existe pas
        Files.createDirectories(uploadPath);

        // Construit le chemin complet du fichier physique à enregistrer
        Path filePath = uploadPath.resolve(uniqueFilename);
        // Transfère le fichier téléchargé vers l'emplacement physique
        file.transferTo(filePath.toFile());

        // Construit l'URL relative pour l'accès web (stockée dans le modèle)
        String pdfUrl = "/uploads/" + uniqueFilename;

        // Construit et enregistre l'examen

        Exam exam = Exam.builder()
                .title(request.title())
                .type(ExamType.valueOf(request.type().toUpperCase()))
                .year(request.year())
                .pdfUrl(pdfUrl) // Seul pdfUrl est stocké dans l'entité Exam
                .subjectId(request.subjectId())
                .createdAt(LocalDate.now())
                .downloadCount(0) // Initialise le compteur de téléchargement
                .build();

        return examRepository.save(exam);
    }

    public List<Exam> filterExamsFlexible(String departmentId, String programId, String levelId, String semesterId, String subjectId) {
        // Logique de filtrage des examens basée sur divers critères
        List<Subject> subjects = subjectRepository.findAll();

        if (semesterId != null) {
            subjects = subjects.stream()
                    .filter(s -> s.getSemesterId().equals(semesterId))
                    .toList();
        }

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

        Set<String> allowedSemesterIds = semesters.stream().map(Semester::getId).collect(Collectors.toSet());
        subjects = subjects.stream()
                .filter(s -> allowedSemesterIds.contains(s.getSemesterId()))
                .toList();

        if (subjectId != null) {
            subjects = subjects.stream()
                    .filter(s -> s.getId().equals(subjectId))
                    .toList();
        }

        Set<String> subjectIds = subjects.stream().map(Subject::getId).collect(Collectors.toSet());
        return examRepository.findBySubjectIdIn(subjectIds);
    }

    public Page<Exam> getAllExams(Pageable pageable) {
        // Récupère tous les examens avec pagination
        return examRepository.findAll(pageable);
    }

    public Optional<Exam> getExamById(String id) {
        // Récupère un examen par son ID
        return examRepository.findById(id);
    }

    public List<Exam> getMostDownloadedExams(int limit) {
        // Récupère les examens les plus téléchargés
        return examRepository.findTopByOrderByDownloadCountDesc(limit);
    }

    public Exam updateExam(String id, Exam examUpdate) {
        // Met à jour un examen existant
        Optional<Exam> existingExamOpt = examRepository.findById(id);

        if (existingExamOpt.isEmpty()) {
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam existingExam = existingExamOpt.get();

        // Met à jour uniquement les champs non nuls de l'objet de mise à jour
        if (examUpdate.getTitle() != null) {
            existingExam.setTitle(examUpdate.getTitle());
        }
        if (examUpdate.getType() != null) {
            existingExam.setType(examUpdate.getType());
        }
        if (examUpdate.getSubjectId() != null) {
            existingExam.setSubjectId(examUpdate.getSubjectId());
        }
        if (examUpdate.getYear() != 0) {
            existingExam.setYear(examUpdate.getYear());
        }
        if (examUpdate.getPdfUrl() != null) {
            existingExam.setPdfUrl(examUpdate.getPdfUrl());
        }
        if (examUpdate.getCreatedAt() != null) {
            existingExam.setCreatedAt(examUpdate.getCreatedAt());
        }

        if (examUpdate.getTags() != null) {
            existingExam.setTags(examUpdate.getTags());
        }

        return examRepository.save(existingExam);
    }

    @SneakyThrows
    public void deleteExam(String id) {
        Optional<Exam> examOpt = examRepository.findById(id);

        if (examOpt.isEmpty()) {
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam exam = examOpt.get();

        // MODIFICATION ICI : Reconstruire le chemin physique à partir de pdfUrl pour la suppression
        if (exam.getPdfUrl() != null) {
            Path filePath = getPhysicalFilePath(exam.getPdfUrl());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Fichier supprimé physiquement : " + filePath.toString());
            } else {
                System.out.println("Le fichier physique n'existe pas à l'emplacement attendu : " + filePath.toString());
            }
        }

        // Supprime l'examen de la base de données
        examRepository.deleteById(id);
        System.out.println("Examen supprimé de la base de données : " + id);
    }


    public void downloadExam(String id, HttpServletResponse response) throws IOException {
        Optional<Exam> examOpt = examRepository.findById(id);

        if (examOpt.isEmpty()) {
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam exam = examOpt.get();

        // MODIFICATION ICI : Reconstruire le chemin physique à partir de pdfUrl pour le téléchargement
        if (exam.getPdfUrl() == null) {
            throw new RuntimeException("URL PDF non trouvée pour l'examen: " + id);
        }

        Path filePath = getPhysicalFilePath(exam.getPdfUrl());

        if (!Files.exists(filePath)) {
            throw new IOException("Fichier non trouvé: " + filePath.toString());
        }

        // Incrémentation atomique du compteur de téléchargement
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("downloadCount", 1);
        mongoTemplate.updateFirst(query, update, Exam.class);
        System.out.println("Compteur de téléchargement incrémenté pour l'examen : " + id);

        // MODIFICATION ICI : Extraire le nom de fichier original de l'URL unique pour l'en-tête de téléchargement
        String uniqueFilenameInUrl = exam.getPdfUrl().substring(exam.getPdfUrl().lastIndexOf('/') + 1);
        String fileNameToDownload = extractOriginalFileName(uniqueFilenameInUrl);

        // Définir les en-têtes de réponse pour le téléchargement
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameToDownload + "\"");
        // La taille du fichier est toujours prise en compte pour le téléchargement, elle est lue directement du fichier physique
        response.setContentLengthLong(Files.size(filePath));

        // Stream du fichier vers la réponse HTTP
        try (InputStream inputStream = Files.newInputStream(filePath);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
    }


    public List<Exam> getRecentDownloadedExams(int limit) {
        // Récupère les examens récemment téléchargés avec pagination et tri
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return examRepository.findAll(pageable).getContent();
    }
}
