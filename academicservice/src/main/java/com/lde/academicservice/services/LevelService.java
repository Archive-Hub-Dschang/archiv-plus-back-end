package com.lde.academicservice.services;

import com.lde.academicservice.models.Level;
import com.lde.academicservice.repositories.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class LevelService {

    @Autowired
    private LevelRepository levelRepository;

    public Level createLevel(Level level) {
        return levelRepository.save(level);
    }

    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    public Optional<Level> getLevelById(String id) {
        return levelRepository.findById(id);
    }

    public Level updateLevel(String id, Level updated) {
        updated.setId(id);
        return levelRepository.save(updated);
    }

    public void deleteLevel(String id) {
        levelRepository.deleteById(id);
    }
}