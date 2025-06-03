package com.lde.academicservice.services;

import com.lde.academicservice.dtos.ExamWithCorrectionDTO;
import com.lde.academicservice.models.*;
import com.lde.academicservice.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExamServiceTest {

    private ExamService examService;

    private SubjectRepository subjectRepository = mock(SubjectRepository.class);
    private SemesterRepository semesterRepository = mock(SemesterRepository.class);
    private ProgramRepository programRepository = mock(ProgramRepository.class);
    private ExamRepository examRepository = mock(ExamRepository.class);
    private CorrectionRepository correctionRepository = mock(CorrectionRepository.class);

    @BeforeEach
    void setUp() {
        examService = new ExamService(
                subjectRepository,
                semesterRepository,
                programRepository,
                examRepository,
                correctionRepository
        );
    }

    @Test
    void shouldReturnExamsWithCorrections() {
        // IDs
        String departmentId = "dept1";
        String programId = "prog1";
        String levelId = "lvl1";
        String semesterId = "sem1";
        String subjectId = "sub1";
        String examId = "exam1";

        // Mock Subject
        Subject subject = new Subject(subjectId, "Math", semesterId);
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        // Mock Semester
        Semester semester = new Semester(semesterId, "Semestre 1", programId, levelId);
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));

        // Mock Program
        Program program = new Program(programId, "Informatique", departmentId);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));

        // Mock Exam
        Exam exam = Exam.builder()
                .id(examId)
                .title("CC1")
                .type(ExamType.CC)
                .year(2024)
                .pdfUrl("http://example.com/cc1.pdf")
                .subjectId(subjectId)
                .createdAt(LocalDate.now())
                .build();

        when(examRepository.findBySubjectIdAndType(subjectId, ExamType.CC))
                .thenReturn(List.of(exam));

        // Mock Correction
        Correction correction = new Correction("corr1", examId, "http://example.com/cc1_corr.pdf", LocalDate.now());
        when(correctionRepository.findByExamId(examId))
                .thenReturn(Optional.of(correction));

        // Call
        List<ExamWithCorrectionDTO> result = examService.getExamsWithCorrections(
                departmentId, programId, levelId, semesterId, subjectId, ExamType.CC
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExam().getTitle()).isEqualTo("CC1");
        assertThat(result.get(0).getCorrection().getPdfUrl()).isEqualTo("http://example.com/cc1_corr.pdf");

        // Verify repository calls
        verify(subjectRepository).findById(subjectId);
        verify(semesterRepository).findById(semesterId);
        verify(programRepository).findById(programId);
        verify(examRepository).findBySubjectIdAndType(subjectId, ExamType.CC);
        verify(correctionRepository).findByExamId(examId);
    }
}
