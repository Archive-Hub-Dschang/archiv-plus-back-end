package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Subject;
import com.lde.academicservice.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/academics/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("/department/{departmentId}/program/{programId}/level/{levelId}/semester/{semesterId}")
    public ResponseEntity<List<Subject>> getSubjectsByFilters(
            @PathVariable String departmentId,
            @PathVariable String programId,
            @PathVariable String levelId,
            @PathVariable String semesterId
    ) {
        List<Subject> subjects = subjectService.getSubjects(departmentId, programId, levelId, semesterId);
        return ResponseEntity.ok(subjects);
    }
}
