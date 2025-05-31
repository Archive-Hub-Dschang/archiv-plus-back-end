package com.lde.usermicroservice.controllers;

import com.lde.usermicroservice.dto.DownloadStatsDTO;
import com.lde.usermicroservice.dto.PaginatedDownloadsDTO;
import com.lde.usermicroservice.dto.PaginationDTO;
import com.lde.usermicroservice.dto.UserProfilDownloadDTO;
import com.lde.usermicroservice.dto.UserProfilDTO;
import com.lde.usermicroservice.dto.UserDownloadHistoryDTO;
import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.services.UserDownloadService;
import com.lde.usermicroservice.services.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final UserDownloadService userDownloadService;

    /**
     * Récupère le profil complet de l'utilisateur authentifié, incluant l'historique de téléchargement et les stats.
     */
    @GetMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfilDownloadDTO> getMyProfilDownloads() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // IMPORTANT : L'ID utilisateur du JWT est un String (l'email).
        // Nous devons d'abord trouver l'utilisateur par son email pour obtenir son ID Long.
        String userEmail = authentication.getName();

        Optional<User> userOpt = userService.getByEmail(userEmail); // Assurez-vous d'avoir cette méthode
        // dans UserService/UserRepository
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        Long userId = user.getId(); // L'ID Long de l'utilisateur

        // Récupérer les 20 derniers téléchargements (ou paginer si nécessaire)
        Pageable defaultPageable = Pageable.ofSize(20).withPage(0);
        Page<UserDownloadHistoryDTO> downloadsPage = userDownloadService.getDownloadHistoryForUser(userId, defaultPageable); // UserDownloadService attend un String
        List<UserDownloadHistoryDTO> downloads = downloadsPage.getContent();

        // Récupérer les statistiques de téléchargement
        DownloadStatsDTO stats = userDownloadService.getDownloadStatsForUser(userId); // UserDownloadService attend un String

        // Construire le DTO de réponse
        UserProfilDTO userProfileDto = UserProfilDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // Plus de firstName, lastName, createdAt, lastLoginAt, roles
                .build();

        UserProfilDownloadDTO profileDTO = UserProfilDownloadDTO.builder()
                .user(userProfileDto)
                .downloadHistory(downloads)
                .downloadStats(stats)
                .build();

        return new ResponseEntity<>(profileDTO, HttpStatus.OK);
    }

    /**
     * Endpoint pour lister l'historique de téléchargements pour l'utilisateur actuellement authentifié, avec pagination.
     */
    @GetMapping("/me/download-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaginatedDownloadsDTO> getMyDownloadHistory(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> userOpt = userService.getByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Long userId = userOpt.get().getId();

        Page<UserDownloadHistoryDTO> downloadRecordsPage = userDownloadService.getDownloadHistoryForUser(userId, pageable);

        PaginationDTO pagination = PaginationDTO.builder()
                .page(downloadRecordsPage.getNumber() + 1)
                .limit(downloadRecordsPage.getSize())
                .total(downloadRecordsPage.getTotalElements())
                .totalPages(downloadRecordsPage.getTotalPages())
                .hasMore(downloadRecordsPage.hasNext())
                .hasPrevious(downloadRecordsPage.hasPrevious())
                .build();

        PaginatedDownloadsDTO responseDTO = PaginatedDownloadsDTO.builder()
                .downloads(downloadRecordsPage.getContent())
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Endpoint pour lister l'historique de téléchargements d'un utilisateur spécifique par son ID.
     * Généralement réservé aux administrateurs ou à des fins de test interne.
     */
    @GetMapping("/{userId}/download-history")
    // Le userId dans le path est maintenant un Long.
    // L'autorisation doit vérifier si l'utilisateur authentifié est ADMIN ou si l'ID correspond.
    @PreAuthorize("hasAuthority('Admin')") // Ou vous devez passer l'email de l'admin à la place de l'ID
    public ResponseEntity<PaginatedDownloadsDTO> getUserDownloadHistory(
            @PathVariable Long userId, // Changement de String à Long
            Pageable pageable) {

        // Comme UserDownloadService attend un String pour userId, nous le convertissons
        Page<UserDownloadHistoryDTO> downloadRecordsPage = userDownloadService.getDownloadHistoryForUser(userId, pageable);

        PaginationDTO pagination = PaginationDTO.builder()
                .page(downloadRecordsPage.getNumber() + 1)
                .limit(downloadRecordsPage.getSize())
                .total(downloadRecordsPage.getTotalElements())
                .totalPages(downloadRecordsPage.getTotalPages())
                .hasMore(downloadRecordsPage.hasNext())
                .hasPrevious(downloadRecordsPage.hasPrevious())
                .build();

        PaginatedDownloadsDTO responseDTO = PaginatedDownloadsDTO.builder()
                .downloads(downloadRecordsPage.getContent())
                .pagination(pagination)
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Endpoint pour obtenir les statistiques de téléchargement pour l'utilisateur authentifié.
     */
    @GetMapping("/me/download-stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DownloadStatsDTO> getMyDownloadStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> userOpt = userService.getByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Long userId = userOpt.get().getId();

        DownloadStatsDTO stats = userDownloadService.getDownloadStatsForUser(userId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Endpoint pour obtenir les statistiques de téléchargement pour un utilisateur spécifique.
     * Réservé aux administrateurs.
     */
    @GetMapping("/{userId}/download-stats")
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseEntity<DownloadStatsDTO> getUserDownloadStats(@PathVariable Long userId) { // Changement de String à Long
        DownloadStatsDTO stats = userDownloadService.getDownloadStatsForUser(userId);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
}