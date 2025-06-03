package com.lde.academicservice.controllers;

import com.lde.academicservice.dto.SemesterResponseDTO;
import com.lde.academicservice.models.Semester;
import com.lde.academicservice.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/semesters")
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

    @GetMapping("/dto/{id}")
    public SemesterResponseDTO getSemesterDtoById(@PathVariable String id) {
        Semester semester = semesterService.getSemesterById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semester not found"));
        return new SemesterResponseDTO(semester.getId(), semester.getEndSemesterDate());
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
