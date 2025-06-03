package com.lde.academicservice.services;

import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(String id) {
        return subjectRepository.findById(id);
    }

    public Subject updateSubject(String id, Subject updated) {
        updated.setId(id);
        return subjectRepository.save(updated);
    }

    public void deleteSubject(String id) {
        subjectRepository.deleteById(id);
    }
}