package com.lde.usermicroservice.Service;

import com.lde.usermicroservice.clients.ExamClient;
import com.lde.usermicroservice.dto.ExamDTO;
import com.lde.usermicroservice.models.Favorite;
import com.lde.usermicroservice.repositories.FavoriteRepository;
import com.lde.usermicroservice.services.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FavoriteServiceTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private ExamClient examClient;

    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFavoriteExams() {
        Long userId = 1L;
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setExamId("exam123");

        ExamDTO examDTO = new ExamDTO();
        examDTO.setId("exam123");

        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(fav));
        when(examClient.getExamById("exam123")).thenReturn(examDTO);

        List<ExamDTO> result = favoriteService.getFavoriteExams(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("exam123");
        verify(favoriteRepository).findByUserId(userId);
        verify(examClient).getExamById("exam123");
    }

    @Test
    void testAddFavorite_whenNotAlreadyExists() {
        Long userId = 2L;
        String examId = "exam999";

        when(favoriteRepository.findByUserIdAndExamId(userId, examId)).thenReturn(null);

        Favorite savedFavorite = new Favorite();
        savedFavorite.setUserId(userId);
        savedFavorite.setExamId(examId);

        when(favoriteRepository.save(any(Favorite.class))).thenReturn(savedFavorite);

        Favorite result = favoriteService.addFavorite(userId, examId);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getExamId()).isEqualTo(examId);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void testAddFavorite_whenAlreadyExists_shouldThrowException() {
        Long userId = 2L;
        String examId = "exam001";
        Favorite existingFavorite = new Favorite();
        existingFavorite.setUserId(userId);
        existingFavorite.setExamId(examId);

        when(favoriteRepository.findByUserIdAndExamId(userId, examId)).thenReturn(existingFavorite);

        assertThrows(IllegalStateException.class, () -> favoriteService.addFavorite(userId, examId));
    }

    @Test
    void testGetFavorites() {
        Long userId = 3L;
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setExamId("examABC");

        when(favoriteRepository.findByUserId(userId)).thenReturn(List.of(fav));

        List<Favorite> result = favoriteService.getFavorites(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExamId()).isEqualTo("examABC");
    }

    @Test
    void testRemoveFavorite_whenExists() {
        Long userId = 4L;
        String examId = "examXYZ";

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setExamId(examId);

        when(favoriteRepository.findByUserIdAndExamId(userId, examId)).thenReturn(fav);

        favoriteService.removeFavorite(userId, examId);

        verify(favoriteRepository).delete(fav);
    }

    @Test
    void testRemoveFavorite_whenNotExists() {
        Long userId = 4L;
        String examId = "examNotExist";

        when(favoriteRepository.findByUserIdAndExamId(userId, examId)).thenReturn(null);

        favoriteService.removeFavorite(userId, examId);

        verify(favoriteRepository, never()).delete(any());
    }
}
