package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<Document, String> {}

