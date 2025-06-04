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
                .downloadCount(0) 
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
      
    public Page<Exam> getAllExams(Pageable pageable) {
        // Récupère tous les examens avec pagination
        return examRepository.findAll(pageable);
    }

    public Optional<Exam> getExamById(String id) {
        // Récupère un examen par son ID
        return examRepository.findById(id);
    } public Exam updateExam(String id, Exam examUpdate) {
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

    public List<Exam> getRecentDownloadedExams(int limit) {
        // Récupère les examens récemment téléchargés avec pagination et tri
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return examRepository.findAll(pageable).getContent();

        // Récupération des corrections
        return exams.stream().map(exam -> {
            Optional<Correction> correction = correctionRepository.findByExamId(exam.getId());
            return new ExamWithCorrectionDTO(exam, correction.orElse(null));
        }).toList();

    }
      
    public List<Exam> getMostDownloadedExams(int limit) {
        // Récupère les examens les plus téléchargés
        return examRepository.findTopByOrderByDownloadCountDesc(limit);
    }

    
}
