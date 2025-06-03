package com.lde.academicservice.services;

import com.lde.academicservice.models.Level;
import com.lde.academicservice.repositories.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {
    private final LevelRepository levelRepository;

    public List<Level> getAllLevels(){
        return levelRepository.findAll();
    }

}
