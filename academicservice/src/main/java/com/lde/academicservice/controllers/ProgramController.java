package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Program;
import com.lde.academicservice.services.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/academics/programs")
public class ProgramController {
    private final ProgramService programService;

    @GetMapping("/department/{departmentId}")
    public List<Program> getProgramsByDepartment(@PathVariable String departmentId) {
        return programService.getProgramsByDepartmentId(departmentId);
    }

}
