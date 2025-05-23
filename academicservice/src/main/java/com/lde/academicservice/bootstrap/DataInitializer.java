package com.lde.academicservice.bootstrap;

import com.lde.academicservice.models.Department;
import com.lde.academicservice.models.Field;
import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.DepartmentRepository;
import com.lde.academicservice.repositories.FieldRepository;
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

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department(null, "Math"));
            departmentRepository.save(new Department(null, "Physics"));
        }

        if (subjectRepository.count() == 0) {
            subjectRepository.save(new Subject(null, "Algebra"));
            subjectRepository.save(new Subject(null, "Mechanics"));
        }

        if (fieldRepository.count() == 0) {
            fieldRepository.save(new Field(null, "Linear Algebra"));
            fieldRepository.save(new Field(null, "Quantum Physics"));
        }
    }
}