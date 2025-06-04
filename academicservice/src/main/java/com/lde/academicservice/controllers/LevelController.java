package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Department;
import com.lde.academicservice.models.Level;
import com.lde.academicservice.services.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/academics/levels")
public class LevelController {
    private final LevelService levelService;

    @GetMapping
    public ResponseEntity<List<Level>> getAllLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }

}
