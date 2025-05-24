package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Semester;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends MongoRepository<Semester, String> {
}
