package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Level;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends MongoRepository<Level, String> {
}
