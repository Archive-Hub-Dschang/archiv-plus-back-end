package com.lde.academicservice.services;

import com.lde.academicservice.models.Program;
import com.lde.academicservice.models.Semester;
import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.ProgramRepository;
import com.lde.academicservice.repositories.SemesterRepository;
import com.lde.academicservice.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final ProgramRepository programRepository;

    public List<Subject> getSubjects(String departmentId, String programId, String levelId, String semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));

        // Vérification du niveau
        if (!semester.getLevelId().equals(levelId)) {
            throw new RuntimeException("Le niveau ne correspond pas à ce semestre");
        }

        // Vérification du programme
        if (!semester.getProgramId().equals(programId)) {
            throw new RuntimeException("Le programme ne correspond pas à ce semestre");
        }

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Programme non trouvé"));

        // Vérification du département
        if (!program.getDepartmentId().equals(departmentId)) {
            throw new RuntimeException("Le département ne correspond pas à ce programme");
        }

        return subjectRepository.findBySemesterId(semesterId);
    }
}
