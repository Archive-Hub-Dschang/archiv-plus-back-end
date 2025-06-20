package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Exam;
import com.lde.academicservice.models.ExamType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository

public interface ExamRepository extends MongoRepository<Exam, String> {
    List<Exam> findBySubjectIdIn(Set<String> subjectId);
    List<Exam> findBySubjectIdAndType(String subjectId, ExamType examType);
}
