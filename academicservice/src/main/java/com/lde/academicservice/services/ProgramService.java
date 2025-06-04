package com.lde.academicservice.services;

import com.lde.academicservice.models.Program;
import com.lde.academicservice.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    public List<Program> getProgramsByDepartmentId(String departmentId) {
        return programRepository.findByDepartmentId(departmentId);
    }

}
