package com.lde.academicservice.services;

import com.lde.academicservice.models.Department;
import com.lde.academicservice.repositories.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(String id) {
        return departmentRepository.findById(id);
    }

    public Department updateDepartment(String id, Department updated) {
        updated.setId(id);
        return departmentRepository.save(updated);
    }

    public void deleteDepartment(String id) {
        departmentRepository.deleteById(id);
    }
}

