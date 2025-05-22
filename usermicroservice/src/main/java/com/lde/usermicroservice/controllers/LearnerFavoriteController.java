package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.models.FavoriteRequest;
import com.lde.usermicroservice.services.LearnerFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
    public class LearnerFavoriteController {
        @Autowired
        private LearnerFavoriteService favoriteService;

        @PostMapping
        public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
            return ResponseEntity.ok(favoriteService.addFavorite(request.getLearnerId(), request.getSubjectId()));
        }

        @GetMapping("/{learnerId}")
        public ResponseEntity<?> getFavorites(@PathVariable Long learnerId) {
            return ResponseEntity.ok(favoriteService.getFavorites(learnerId));
        }

        @DeleteMapping
        public ResponseEntity<?> removeFavorite(@RequestParam Long learnerId, @RequestParam String subjectId) {
            favoriteService.removeFavorite(learnerId, subjectId);
            return ResponseEntity.ok().build();
        }
}
