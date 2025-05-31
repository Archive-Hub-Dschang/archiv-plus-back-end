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
import com.mongodb.client.result.UpdateResult; // Import pour UpdateResult

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

    private static final String BASE_UPLOAD_DIR = "C:/fichiers";

    private Path getPhysicalFilePath(String pdfUrl) {
        if (pdfUrl == null || !pdfUrl.startsWith("/uploads/")) {
            throw new IllegalArgumentException("Format d'URL PDF invalide: " + pdfUrl);
        }
        String uniqueFilename = pdfUrl.substring("/uploads/".length());
        return Paths.get(BASE_UPLOAD_DIR, uniqueFilename);
    }

    private String extractOriginalFileName(String uniqueFilename) {
        int underscoreIndex = uniqueFilename.indexOf('_');
        if (underscoreIndex != -1 && underscoreIndex < uniqueFilename.length() - 1) {
            return uniqueFilename.substring(underscoreIndex + 1);
        }
        return uniqueFilename;
    }

    public Exam createExam(CreateExamRequest request) throws IOException {
        MultipartFile file = request.pdf();

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(BASE_UPLOAD_DIR);
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(uniqueFilename);
        file.transferTo(filePath.toFile());

        String pdfUrl = "/uploads/" + uniqueFilename;

        Exam exam = Exam.builder()
                .title(request.title())
                .type(ExamType.valueOf(request.type().toUpperCase()))
                .year(request.year())
                .pdfUrl(pdfUrl)
                .subjectId(request.subjectId())
                .createdAt(LocalDate.now())
                .downloadCount(0)
                .build();

        return examRepository.save(exam);
    }

    public List<Exam> filterExamsFlexible(String departmentId, String programId, String levelId, String semesterId, String subjectId) {
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
        return examRepository.findAll(pageable);
    }

    public Optional<Exam> getExamById(String id) {
        return examRepository.findById(id);
    }

    public List<Exam> getMostDownloadedExams(int limit) {
        return examRepository.findTopByOrderByDownloadCountDesc(limit);
    }

    public Exam updateExam(String id, Exam examUpdate) {
        Optional<Exam> existingExamOpt = examRepository.findById(id);

        if (existingExamOpt.isEmpty()) {
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam existingExam = existingExamOpt.get();

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


        return examRepository.save(existingExam);
    }

    @SneakyThrows
    public void deleteExam(String id) {
        Optional<Exam> examOpt = examRepository.findById(id);

        if (examOpt.isEmpty()) {
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam exam = examOpt.get();

        if (exam.getPdfUrl() != null) {
            Path filePath = getPhysicalFilePath(exam.getPdfUrl());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("DEBUG: Fichier supprimé physiquement : " + filePath.toString());
            } else {
                System.out.println("DEBUG: Le fichier physique n'existe pas à l'emplacement attendu pour suppression : " + filePath.toString());
            }
        }

        examRepository.deleteById(id);
        System.out.println("DEBUG: Examen supprimé de la base de données : " + id);
    }


    public void downloadExam(String id, HttpServletResponse response) throws IOException {
        System.out.println("DEBUG: Début du téléchargement pour l'examen ID : " + id);
        Optional<Exam> examOpt = examRepository.findById(id);

        if (examOpt.isEmpty()) {
            System.err.println("ERREUR: Examen non trouvé avec l'id: " + id);
            throw new RuntimeException("Examen non trouvé avec l'id: " + id);
        }

        Exam exam = examOpt.get();
        System.out.println("DEBUG: Examen trouvé : " + exam.getTitle() + " (ID: " + exam.getId() + ")");

        if (exam.getPdfUrl() == null) {
            System.err.println("ERREUR: URL PDF non trouvée pour l'examen: " + id);
            throw new RuntimeException("URL PDF non trouvée pour l'examen: " + id);
        }

        Path filePath = getPhysicalFilePath(exam.getPdfUrl());
        System.out.println("DEBUG: Chemin physique du fichier : " + filePath.toString());

        if (!Files.exists(filePath)) {
            System.err.println("ERREUR: Fichier physique non trouvé à l'emplacement: " + filePath.toString());
            throw new IOException("Fichier non trouvé: " + filePath.toString());
        }
        System.out.println("DEBUG: Fichier physique trouvé et accessible.");

        // Incrémentation atomique du compteur de téléchargement
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().inc("downloadCount", 1);

        // Exécuter la mise à jour et vérifier le résultat
        UpdateResult result = mongoTemplate.updateFirst(query, update, Exam.class);
        if (result.wasAcknowledged() && result.getModifiedCount() > 0) {
            System.out.println("DEBUG: Compteur de téléchargement incrémenté avec succès pour l'examen : " + id + ". Documents modifiés : " + result.getModifiedCount());
        } else {
            System.err.println("AVERTISSEMENT: Échec de l'incrémentation du compteur de téléchargement pour l'examen : " + id + ". Documents modifiés : " + result.getModifiedCount() + ". Acknowledged : " + result.wasAcknowledged());
        }

        String uniqueFilenameInUrl = exam.getPdfUrl().substring(exam.getPdfUrl().lastIndexOf('/') + 1);
        String fileNameToDownload = extractOriginalFileName(uniqueFilenameInUrl);
        System.out.println("DEBUG: Nom de fichier pour le téléchargement : " + fileNameToDownload);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileNameToDownload + "\"");
        response.setContentLengthLong(Files.size(filePath));
        System.out.println("DEBUG: En-têtes de réponse définis. Taille du fichier : " + Files.size(filePath) + " octets.");

        try (InputStream inputStream = Files.newInputStream(filePath);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0; // Pour suivre les octets réellement lus et écrits

            System.out.println("DEBUG: Début du streaming du fichier...");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            outputStream.flush();
            System.out.println("DEBUG: Streaming du fichier terminé. Octets transférés : " + totalBytesRead);
        } catch (IOException e) {
            System.err.println("ERREUR: Erreur d'E/S pendant le streaming du fichier " + fileNameToDownload + " (ID: " + id + ") : " + e.getMessage());
            e.printStackTrace(); // Affiche la stack trace complète
            // Il est important de ne pas relancer l'exception ici si la réponse a déjà commencé,
            // car le client pourrait déjà avoir une connexion fermée.
        }
        System.out.println("DEBUG: Fin de la méthode downloadExam pour l'examen ID : " + id);
    }


    public List<Exam> getRecentDownloadedExams(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createAt"));
        return examRepository.findAll(pageable).getContent();
    }
}
