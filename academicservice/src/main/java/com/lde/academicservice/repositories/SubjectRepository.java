package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findBySemesterId(String semesterId);
}
