package com.lde.academicservice.bootstrap;

import com.lde.academicservice.models.Department;
import com.lde.academicservice.models.Field;
import com.lde.academicservice.models.Level;
import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.DepartmentRepository;
import com.lde.academicservice.repositories.FieldRepository;
import com.lde.academicservice.repositories.LevelRepository;
import com.lde.academicservice.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final FieldRepository fieldRepository;
    private final LevelRepository levelRepository;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department(null, "Math"));
            departmentRepository.save(new Department(null, "Physics"));
        }

        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject(null, "Algebra","dcours d'algebre","INF210"));
            subjectRepository.save(new Subject(null, "Mechanics","cours mecanic","INF211"));
        }

        if (fieldRepository.count() == 0) {
            fieldRepository.save(new Field(null, "Linear Algebra"));
            fieldRepository.save(new Field(null, "Quantum Physics"));
        }
        if (levelRepository.count() == 0) {
            levelRepository.save(new Level(null,"niveau1","L1"));
           levelRepository.save(new Level(null, "niveau2","L2"));
        }
    }
}