package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Semester;
import com.lde.academicservice.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/semesters")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @PostMapping
    public Semester createSemester(@RequestBody Semester semester) {
        return semesterService.createSemester(semester);
    }

    @GetMapping
    public List<Semester> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    @GetMapping("/{id}")
    public Optional<Semester> getSemesterById(@PathVariable String id) {
        return semesterService.getSemesterById(id);
    }

    @PutMapping("/{id}")
    public Semester updateSemester(@PathVariable String id, @RequestBody Semester updated) {
        return semesterService.updateSemester(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteSemester(@PathVariable String id) {
        semesterService.deleteSemester(id);
    }
}
