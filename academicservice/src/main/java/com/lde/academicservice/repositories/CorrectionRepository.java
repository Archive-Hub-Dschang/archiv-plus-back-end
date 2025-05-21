package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Correction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrectionRepository extends MongoRepository<Correction, String> {
}
