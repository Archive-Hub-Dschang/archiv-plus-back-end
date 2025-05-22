package com.lde.usermicroservice.services;

import com.lde.usermicroservice.clients.SubjectClient;
import com.lde.usermicroservice.dtos.SubjectDTO;
import com.lde.usermicroservice.models.LearnerFavorite;
import com.lde.usermicroservice.repositories.LearnerFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearnerFavoriteService {
    private LearnerFavoriteRepository favoriteRepository;

    private SubjectClient subjectClient;

    public List<SubjectDTO> getFavoriteSubjects(Long learnerId) {
        List<LearnerFavorite> favorites = favoriteRepository.findByLearnerId(learnerId);
        return favorites.stream()
                .map(fav -> subjectClient.getSubjectById(fav.getSubjectId()))
                .collect(Collectors.toList());
    }

//methode pour ajouter un favoris
    public LearnerFavorite addFavorite(Long learnerId, String subjectId) {
        Optional<LearnerFavorite> existing = favoriteRepository.findByLearnerIdAndSubjectId(learnerId, subjectId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Sujet déjà ajouté aux favoris");
        }
        LearnerFavorite favorite = new LearnerFavorite();
        favorite.setLearnerId(learnerId);
        favorite.setSubjectId(subjectId);
        favorite.setAddedAt(LocalDateTime.now());

        return favoriteRepository.save(favorite);
    }
//    methode pour avoir tous les favorits d'un utlisateur
public List<LearnerFavorite> getFavorites(Long learnerId) {

    return favoriteRepository.findByLearnerId(learnerId);
}
//methode pour suprimmer un sujet des favoris
    public void removeFavorite(Long learnerId, String subjectId) {
        favoriteRepository.findByLearnerIdAndSubjectId(learnerId, subjectId)
                .ifPresent(favoriteRepository::delete);
    }
}
