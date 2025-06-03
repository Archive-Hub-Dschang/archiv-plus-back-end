package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Subject;
import com.lde.academicservice.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{id}")
    public Optional<Subject> getSubjectById(@PathVariable String id) {
        return subjectService.getSubjectById(id);
    }

    @PutMapping("/{id}")
    public Subject updateSubject(@PathVariable String id, @RequestBody Subject updated) {
        return subjectService.updateSubject(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable String id) {
        subjectService.deleteSubject(id);
    }
}
