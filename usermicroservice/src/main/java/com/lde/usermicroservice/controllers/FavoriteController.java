package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.FavoriteRequestDTO;
import com.lde.usermicroservice.models.Favorite;
import com.lde.usermicroservice.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(@RequestBody FavoriteRequestDTO request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request.getUserId(), request.getExamId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }

    @DeleteMapping
    public ResponseEntity<String> removeFavorite(@RequestParam Long userId, @RequestParam String examId) {
        favoriteService.removeFavorite(userId, examId);
        return ResponseEntity.ok("favoris supprimé avec succes");
    }
}
