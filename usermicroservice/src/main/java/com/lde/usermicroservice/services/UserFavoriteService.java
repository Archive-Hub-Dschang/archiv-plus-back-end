package com.lde.usermicroservice.services;

import com.lde.usermicroservice.clients.DocumentClient;
import com.lde.usermicroservice.dto.DocumentDTO;
import com.lde.usermicroservice.models.UserFavorite;
import com.lde.usermicroservice.repositories.UserFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFavoriteService {
    private final UserFavoriteRepository favoriteRepository;
    private DocumentClient documentClient;

    public List<DocumentDTO> getFavoriteDocuments(String userId) {
        List<UserFavorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(fav -> documentClient.getDocumentById(fav.getDocumentId()))
                .collect(Collectors.toList());
    }

    //methode pour ajouter un favoris
    public UserFavorite addFavorite(String userId, String documentId) {
        UserFavorite potentialuserFavorite = favoriteRepository.findByUserIdAndDocumentId(userId, documentId);
        if (potentialuserFavorite != null) {
            throw new IllegalStateException("document deja ajouté aux favoris");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setDocumentId(documentId);

        return favoriteRepository.save(favorite);
    }

    //    methode pour avoir tous les favorits d'un utlisateur
    public List<UserFavorite> getFavorites(String userId) {
        return favoriteRepository.findByUserId(userId);

    }

    //methode pour suprimmer un sujet des favoris
    public void removeFavorite(String userId, String documentId) {
        UserFavorite potentialuserFavorite = favoriteRepository.findByUserIdAndDocumentId(userId, documentId);
        if (potentialuserFavorite != null) {
            favoriteRepository.delete(potentialuserFavorite);
        }
    }
}
