package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Program;
import com.lde.academicservice.services.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/programs")
public class ProgramController {

    @Autowired
    private ProgramService programService;

    @PostMapping
    public Program createProgram(@RequestBody Program program) {
        return programService.createProgram(program);
    }

    @GetMapping
    public List<Program> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/{id}")
    public Optional<Program> getProgramById(@PathVariable String id) {
        return programService.getProgramById(id);
    }

    @PutMapping("/{id}")
    public Program updateProgram(@PathVariable String id, @RequestBody Program updated) {
        return programService.updateProgram(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteProgram(@PathVariable String id) {
        programService.deleteProgram(id);
    }
}
