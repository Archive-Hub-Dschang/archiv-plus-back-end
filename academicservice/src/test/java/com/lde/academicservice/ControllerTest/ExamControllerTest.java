package com.lde.academicservice.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lde.academicservice.controllers.ExamController;
import com.lde.academicservice.dto.CreateExamRequest;
import com.lde.academicservice.models.Exam;
import com.lde.academicservice.models.ExamType;
import com.lde.academicservice.services.ExamService;
import jakarta.servlet.http.HttpServletResponse; // Ensure this import is present
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus; // Added for ResponseStatusException
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException; // Added for ResponseStatusException

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamService examService;

    private ObjectMapper objectMapper;

    // Test Data
    private Exam exam1;
    private Exam exam2;
    private CreateExamRequest createExamRequest;
    private MockMultipartFile mockPdfFile;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Initialize test Exam objects
        exam1 = new Exam();
        exam1.setId("examId1");
        exam1.setTitle("Calculus Midterm");
        exam1.setType(ExamType.CC);
        exam1.setYear(2023);
        exam1.setPdfUrl("/uploads/calc_2023.pdf");
        exam1.setSubjectId("math101");
        exam1.setCreatedAt(LocalDate.now());
        exam1.setDownloadCount(10);
        exam1.setTags(new String[]{"calculus", "midterm"});

        exam2 = new Exam();
        exam2.setId("examId2");
        exam2.setTitle("Physics Final");
        exam2.setType(ExamType.EXAM);
        exam2.setYear(2024);
        exam2.setPdfUrl("/uploads/phys_2024.pdf");
        exam2.setSubjectId("phys202");
        exam2.setCreatedAt(LocalDate.now().minusDays(30));
        exam2.setDownloadCount(5);
        exam2.setTags(new String[]{"physics", "final"});

        // Initialize multipart file for creation tests
        mockPdfFile = new MockMultipartFile(
                "pdf",
                "new-exam.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "new exam content".getBytes()
        );

        // Initialize CreateExamRequest - ensure parameter order matches the record definition
        createExamRequest = new CreateExamRequest(
                "New Test Exam",
                mockPdfFile,
                2025,
                "cs101",
                "CC",
                5
        );
        when(examService.createExam(any(CreateExamRequest.class)))
                .thenAnswer(invocation -> {
                    CreateExamRequest request = invocation.getArgument(0); // Get the CreateExamRequest passed to the service
                    Exam newExam = new Exam();
                    newExam.setId("newlyGeneratedId"); // Simulate a new ID being generated
                    newExam.setTitle(request.title());
                    newExam.setType(ExamType.valueOf(request.type())); // Convert String to ExamType
                    newExam.setYear(request.year());
                    newExam.setPdfUrl("/uploads/" + request.pdf().getOriginalFilename()); // Simulate PDF URL
                    newExam.setSubjectId(request.subjectId());
                    newExam.setDownloadCount(request.downloadCount());
                    newExam.setCreatedAt(LocalDate.now()); // Set a creation date
                    newExam.setTags(new String[]{"test"}); // Or generate based on title, etc.
                    return newExam;
                });

        when(examService.getExamById(eq("examId1"))).thenReturn(Optional.of(exam1));
        when(examService.getExamById(eq("nonExistentId"))).thenReturn(Optional.empty());

        Page<Exam> examPage = new PageImpl<>(Arrays.asList(exam1, exam2));
        when(examService.getAllExams(any(Pageable.class))).thenReturn(examPage);

        when(examService.updateExam(eq("examId1"), any(Exam.class)))
                .thenAnswer(invocation -> {
                    Exam updated = invocation.getArgument(1);
                    Exam returnedExam = new Exam();
                    returnedExam.setId(exam1.getId());
                    returnedExam.setTitle(updated.getTitle() != null ? updated.getTitle() : exam1.getTitle());
                    returnedExam.setType(updated.getType() != null ? updated.getType() : exam1.getType());
                    returnedExam.setYear(updated.getYear() != 0 ? updated.getYear() : exam1.getYear());
                    returnedExam.setPdfUrl(exam1.getPdfUrl());
                    returnedExam.setSubjectId(exam1.getSubjectId());
                    returnedExam.setCreatedAt(exam1.getCreatedAt());
                    returnedExam.setDownloadCount(exam1.getDownloadCount());
                    returnedExam.setTags(updated.getTags() != null ? updated.getTags() : exam1.getTags());
                    return returnedExam;
                });
        // Mock to throw 404 for non-existent exam update
        when(examService.updateExam(eq("nonExistentId"), any(Exam.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Examen non trouvé pour la mise à jour"));

        doNothing().when(examService).deleteExam(eq("examId1"));
        // Mock to throw 404 for non-existent exam deletion
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Examen non trouvé pour la suppression"))
                .when(examService).deleteExam(eq("nonExistentId"));

        List<Exam> filteredExams = Collections.singletonList(exam1);
        when(examService.filterExamsFlexible(any(), any(), any(), any(), eq("math101")))
                .thenReturn(filteredExams);

        List<Exam> popularExams = Arrays.asList(exam1, exam2);
        when(examService.getMostDownloadedExams(anyInt())).thenReturn(popularExams);

        List<Exam> recentExams = Arrays.asList(exam2, exam1);
        when(examService.getRecentDownloadedExams(anyInt())).thenReturn(recentExams);

        doNothing().when(examService).downloadExam(eq("examId1"), any(HttpServletResponse.class));
        // Mock to throw 404 for non-existent exam download
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Examen non trouvé pour le téléchargement"))
                .when(examService).downloadExam(eq("nonExistentId"), any(HttpServletResponse.class));
    }
    @Test
    @DisplayName("POST /api/exams - Devrait créer un nouvel examen avec succès")
    void testCreateExamSuccess() throws Exception {
        mockMvc.perform(multipart("/api/exams")
                        .file(mockPdfFile)
                        .param("title", createExamRequest.title())
                        .param("year", String.valueOf(createExamRequest.year()))
                        .param("subjectId", createExamRequest.subjectId())
                        .param("type", createExamRequest.type())
                        .param("downloadCount", String.valueOf(createExamRequest.downloadCount())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(createExamRequest.title()));

        verify(examService).createExam(any(CreateExamRequest.class));
    }

    @Test
    @DisplayName("GET /api/exams/{id} - Should return an exam by ID")
    void testGetExamByIdSuccess() throws Exception {
        mockMvc.perform(get("/api/exams/{id}", "examId1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exam1.getId()))
                .andExpect(jsonPath("$.title").value(exam1.getTitle()))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(examService).getExamById("examId1");
    }

    @Test
    @DisplayName("GET /api/exams/{id} - Should return 404 if exam not found")
    void testGetExamByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/exams/{id}", "nonExistentId")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(examService).getExamById("nonExistentId");
    }

    @Test
    @DisplayName("GET /api/exams - Should return all exams with pagination")
    void testGetAllExams() throws Exception {
        mockMvc.perform(get("/api/exams")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(examService).getAllExams(any(Pageable.class));
    }

    @Test
    @DisplayName("PUT /api/exams/{id} - Should update an existing exam successfully")
    void testUpdateExamSuccess() throws Exception {
        Exam updatedFields = new Exam();
        updatedFields.setTitle("Calculus Midterm Revised");
        updatedFields.setYear(2024);
        updatedFields.setType(ExamType.EXAM);
        updatedFields.setCreatedAt(LocalDate.now()); // Ensure createdAt is set

        mockMvc.perform(put("/api/exams/{id}", "examId1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFields)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exam1.getId()))
                .andExpect(jsonPath("$.title").value("Calculus Midterm Revised"))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.type").value(ExamType.EXAM.name()));

        verify(examService).updateExam(eq("examId1"), any(Exam.class));
    }

    @Test
    @DisplayName("PUT /api/exams/{id} - Should return 404 if exam to update is not found")
    void testUpdateExamNotFound() throws Exception {
        Exam updatedFields = new Exam();
        updatedFields.setTitle("Update for non-existent exam");
        updatedFields.setCreatedAt(LocalDate.now()); // Ensure createdAt is set

        mockMvc.perform(put("/api/exams/{id}", "nonExistentId") // Correct URL path
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFields)))
                .andExpect(status().isNotFound());

        verify(examService).updateExam(eq("nonExistentId"), any(Exam.class));
    }

    @Test
    @DisplayName("DELETE /api/exams/{id} - Should delete an exam successfully")
    void testDeleteExamSuccess() throws Exception {
        mockMvc.perform(delete("/api/exams/{id}", "examId1"))
                .andExpect(status().isNoContent());

        verify(examService).deleteExam("examId1");
    }

    @Test
    @DisplayName("DELETE /api/exams/{id} - Should return 404 if exam to delete is not found")
    void testDeleteExamNotFound() throws Exception {
        mockMvc.perform(delete("/api/exams/{id}", "nonExistentId"))
                .andExpect(status().isNotFound());

        verify(examService).deleteExam("nonExistentId");
    }

    @Test
    @DisplayName("GET /api/exams/filter - Should return filtered exams by subjectId")
    void testGetFilteredExams() throws Exception {
        mockMvc.perform(get("/api/exams/filter")
                        .param("subjectId", "math101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(exam1.getId()));

        verify(examService).filterExamsFlexible(any(), any(), any(), any(), eq("math101"));
    }

    @Test
    @DisplayName("GET /api/exams/popular - Should return most downloaded exams")
    void testGetPopularExams() throws Exception {
        mockMvc.perform(get("/api/exams/popular")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(examService).getMostDownloadedExams(2);
    }

    @Test
    @DisplayName("GET /api/exams/recent - Should return recently downloaded exams")
    void testGetRecentExams() throws Exception {
        mockMvc.perform(get("/api/exams/recent")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(examService).getRecentDownloadedExams(2);
    }

    @Test
    @DisplayName("GET /api/exams/download/{id} - Should allow downloading an exam")
    void testDownloadExamSuccess() throws Exception {
        mockMvc.perform(get("/api/exams/download/{id}", "examId1"))
                .andExpect(status().isOk());

        verify(examService).downloadExam(eq("examId1"), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("GET /api/exams/download/{id} - Should return 404 if exam to download is not found")
    void testDownloadExamNotFound() throws Exception {
        mockMvc.perform(get("/api/exams/download/{id}", "nonExistentId"))
                .andExpect(status().isNotFound());

        verify(examService).downloadExam(eq("nonExistentId"), any(HttpServletResponse.class));
    }
}