package com.lde.academicservice.controllers;

import com.lde.academicservice.models.Level;
import com.lde.academicservice.services.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/levels")
public class LevelController {

    @Autowired
    private LevelService levelService;

    @PostMapping
    public Level createLevel(@RequestBody Level level) {
        return levelService.createLevel(level);
    }

    @GetMapping
    public List<Level> getAllLevels() {
        return levelService.getAllLevels();
    }

    @GetMapping("/{id}")
    public Optional<Level> getLevelById(@PathVariable String id) {
        return levelService.getLevelById(id);
    }

    @PutMapping("/{id}")
    public Level updateLevel(@PathVariable String id, @RequestBody Level updated) {
        return levelService.updateLevel(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteLevel(@PathVariable String id) {
        levelService.deleteLevel(id);
    }
}
