package com.lde.academicservice.services;

import com.lde.academicservice.models.Semester;
import com.lde.academicservice.repositories.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class SemesterService {

    @Autowired
    private SemesterRepository semesterRepository;

    public Semester createSemester(Semester semester) {
        return semesterRepository.save(semester);
    }

    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    public Optional<Semester> getSemesterById(String id) {
        return semesterRepository.findById(id);
    }

    public Semester updateSemester(String id, Semester updated) {
        updated.setId(id);
        return semesterRepository.save(updated);
    }

    public void deleteSemester(String id) {
        semesterRepository.deleteById(id);
    }
}