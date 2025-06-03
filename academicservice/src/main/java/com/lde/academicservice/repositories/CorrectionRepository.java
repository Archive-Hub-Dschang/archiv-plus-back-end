package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Correction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorrectionRepository extends MongoRepository<Correction, String> {
    boolean existsByExamId(String examId);

    Optional<Correction> findByExamId(String examId);

}
