package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends MongoRepository<Field, String> {
}
