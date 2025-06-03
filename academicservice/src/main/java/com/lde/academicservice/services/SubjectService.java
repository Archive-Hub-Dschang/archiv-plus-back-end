package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Semester;
import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.SemesterRepository;
import com.lde.academicservice.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    public List<Subject> getSubjects(String departmentId, String programId, String levelId, String semesterId) {
        Optional<Semester> semester = semesterRepository.findById(semesterId);

        if (semester.isEmpty()) {
            throw new RuntimeException("Semestre non trouvé");
        }

        Semester sem = semester.get();

        // Vérifie si le semestre correspond bien au programme, niveau, etc.
        if (!sem.getProgramId().equals(programId) || !sem.getLevelId().equals(levelId)) {
            throw new RuntimeException("Incohérence dans les IDs fournis");
        }

        // Tu pourrais aussi ajouter une vérification du programme -> department ici si besoin.

        return subjectRepository.findBySemesterId(semesterId);
    }
}
