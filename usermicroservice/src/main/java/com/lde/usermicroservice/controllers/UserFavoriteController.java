package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.FavoriteRequestDTO;
import com.lde.usermicroservice.models.UserFavorite;
import com.lde.usermicroservice.services.UserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {
    private final UserFavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<UserFavorite> addFavorite(@RequestBody FavoriteRequestDTO request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request.getUserId(), request.getDocumentId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserFavorite>> getFavorites(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    @DeleteMapping
    public ResponseEntity<String> removeFavorite(@RequestParam String userId, @RequestParam String documentId) {
        favoriteService.removeFavorite(userId, documentId);
        return ResponseEntity.ok("favoris supprimé avec succes");
    }
}
