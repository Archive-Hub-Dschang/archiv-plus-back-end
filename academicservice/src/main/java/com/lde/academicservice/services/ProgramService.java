package com.lde.academicservice.services;

import com.lde.academicservice.models.Program;
import com.lde.academicservice.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    public Program createProgram(Program program) {
        return programRepository.save(program);
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public Optional<Program> getProgramById(String id) {
        return programRepository.findById(id);
    }

    public Program updateProgram(String id, Program updated) {
        updated.setId(id);
        return programRepository.save(updated);
    }

    public void deleteProgram(String id) {
        programRepository.deleteById(id);
    }
}