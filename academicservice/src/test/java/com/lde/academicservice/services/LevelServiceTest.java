package com.lde.academicservice.services;

import com.lde.academicservice.models.Level;
import com.lde.academicservice.repositories.LevelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class LevelServiceTest {
    @Test
    public void testGetAllLevels() {

        LevelRepository mockRepo = mock(LevelRepository.class);


        Level l1 = new Level("1","Licence 1");
        Level l2 = new Level("2","Licence 2");

        when(mockRepo.findAll()).thenReturn(Arrays.asList(l1, l2));

        LevelService service = new LevelService(mockRepo);

        List<Level> result = service.getAllLevels();

        assertEquals(2, result.size());

        assertEquals("Licence 1", result.getFirst().getLabel());
    }

}
