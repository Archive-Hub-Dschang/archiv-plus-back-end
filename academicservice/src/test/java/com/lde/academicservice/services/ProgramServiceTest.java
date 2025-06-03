package com.lde.academicservice.services;

import com.lde.academicservice.repositories.ProgramRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


import com.lde.academicservice.models.Program;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProgramServiceTest {

    @Test
    void testGetProgramsByDepartmentId() {
        ProgramRepository mockRepo = mock(ProgramRepository.class);

        Program f1 = new Program("1", "Biologie animale", "3");
        Program f2 = new Program("2", "Biologie Vegetale", "3");

        when(mockRepo.findByDepartmentId("3")).thenReturn(Arrays.asList(f1, f2));

        ProgramService service = new ProgramService(mockRepo);
        List<Program> programs = service.getProgramsByDepartmentId("dep1");
        assertEquals(2, programs.size());
        assertEquals("Biologie animale", programs.getFirst().getName());
    }
}
