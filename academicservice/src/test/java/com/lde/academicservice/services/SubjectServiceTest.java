package com.lde.academicservice.services;

import com.lde.academicservice.models.*;
import com.lde.academicservice.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private SubjectService subjectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnSubjects_whenAllIdsAreValid() {
        // Given
        String departmentId = "dep1";
        String programId = "prog1";
        String levelId = "lvl1";
        String semesterId = "sem1";

        Semester semester = new Semester(semesterId, "Semestre 1", programId, levelId);
        Program program = new Program(programId, "Informatique", departmentId);

        List<Subject> subjects = List.of(
                new Subject("sub1", "Math", semesterId),
                new Subject("sub2", "Physique", semesterId)
        );

        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(programRepository.findById(programId)).thenReturn(Optional.of(program));
        when(subjectRepository.findBySemesterId(semesterId)).thenReturn(subjects);

        // When
        List<Subject> result = subjectService.getSubjects(departmentId, programId, levelId, semesterId);

        // Then
        assertEquals(2, result.size());
        verify(subjectRepository, times(1)).findBySemesterId(semesterId);
    }

    @Test
    void shouldThrow_whenSemesterNotFound() {
        when(semesterRepository.findById("sem1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjects("dep1", "prog1", "lvl1", "sem1");
        });

        assertEquals("Semestre non trouvé", exception.getMessage());
    }

    @Test
    void shouldThrow_whenLevelMismatch() {
        Semester semester = new Semester("sem1", "Semestre 1", "prog1", "lvlX");
        when(semesterRepository.findById("sem1")).thenReturn(Optional.of(semester));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjects("dep1", "prog1", "lvl1", "sem1");
        });

        assertEquals("Le niveau ne correspond pas à ce semestre", exception.getMessage());
    }

    @Test
    void shouldThrow_whenProgramMismatch() {
        Semester semester = new Semester("sem1", "Semestre 1", "progX", "lvl1");
        when(semesterRepository.findById("sem1")).thenReturn(Optional.of(semester));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjects("dep1", "prog1", "lvl1", "sem1");
        });

        assertEquals("Le programme ne correspond pas à ce semestre", exception.getMessage());
    }

    @Test
    void shouldThrow_whenDepartmentMismatch() {
        Semester semester = new Semester("sem1", "Semestre 1", "prog1", "lvl1");
        Program program = new Program("prog1", "Informatique", "depX");

        when(semesterRepository.findById("sem1")).thenReturn(Optional.of(semester));
        when(programRepository.findById("prog1")).thenReturn(Optional.of(program));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            subjectService.getSubjects("dep1", "prog1", "lvl1", "sem1");
        });

        assertEquals("Le département ne correspond pas à ce programme", exception.getMessage());
    }
}
