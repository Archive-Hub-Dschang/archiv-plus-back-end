package com.lde.academicservice.services;

import com.lde.academicservice.models.Department;
import com.lde.academicservice.repositories.DepartmentRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DepartmentServiceTest {

    @Test
    public void testGetAllDepartments() {

        DepartmentRepository mockRepo = mock(DepartmentRepository.class);


        Department d1 = new Department("1","Informatique");
        Department d2 = new Department("2","Mathématiques");

        when(mockRepo.findAll()).thenReturn(Arrays.asList(d1, d2));

        DepartmentService service = new DepartmentService(mockRepo);

        List<Department> result = service.getAllDepartments();

        assertEquals(2, result.size());

        assertEquals("Informatique", result.getFirst().getName());
    }

}
