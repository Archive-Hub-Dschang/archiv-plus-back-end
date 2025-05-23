package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends MongoRepository<Document, String> {
}
