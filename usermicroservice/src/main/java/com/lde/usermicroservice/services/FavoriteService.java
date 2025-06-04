package com.lde.usermicroservice.services;

import com.lde.usermicroservice.clients.ExamClient;
import com.lde.usermicroservice.dto.ExamDTO;
import com.lde.usermicroservice.models.Favorite;
import com.lde.usermicroservice.repositories.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ExamClient examClient;

    public List<ExamDTO> getFavoriteExams(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(fav -> examClient.getExamById(fav.getExamId()))
                .collect(Collectors.toList());
    }

    //methode pour ajouter un favoris
    public Favorite addFavorite(Long userId, String examId) {
        Favorite potentialuserFavorite = favoriteRepository.findByUserIdAndExamId(userId,examId);
        if (potentialuserFavorite != null) {
            throw new IllegalStateException("exam deja ajouté aux favoris");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setExamId(examId);

        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getFavorites(Long userId){
        return favoriteRepository.findByUserId(userId);
    }

    //methode pour suprimmer un sujet des favoris
    public void removeFavorite(Long userId, String examId) {
        Favorite potentialuserFavorite = favoriteRepository.findByUserIdAndExamId(userId, examId);
        if (potentialuserFavorite != null) {
            favoriteRepository.delete(potentialuserFavorite);
        }
    }
}
