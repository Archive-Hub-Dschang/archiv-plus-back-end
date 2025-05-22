package com.lde.academicservice.service;
import com.lde.academicservice.models.Subject;
import com.lde.academicservice.repositories.SubjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject addSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public Subject updateSubject(Long id, Subject updatedSubject) {
        return subjectRepository.findById(id).map(subject -> {
            subject.setName(updatedSubject.getName());
            subject.setCorrection_id(updatedSubject.getCorrection_id());
            subject.setFile_path(updatedSubject.getFile_path());
            return subjectRepository.save(subject);
        }).orElse(null);
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}