package com.lde.academicservice.repositories;

import com.lde.academicservice.models.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
}
