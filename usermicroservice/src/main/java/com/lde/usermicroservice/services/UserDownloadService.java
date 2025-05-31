package com.lde.usermicroservice.services;

import com.lde.usermicroservice.clients.DocumentClient;
import com.lde.usermicroservice.dto.DocumentDTO;
import com.lde.usermicroservice.dto.DownloadEventDTO;
import com.lde.usermicroservice.dto.DownloadStatsDTO;
import com.lde.usermicroservice.dto.UserDownloadHistoryDTO;
import com.lde.usermicroservice.models.DownloadRecord;
import com.lde.usermicroservice.models.User;
import com.lde.usermicroservice.repositories.DownloadRecordRepository;
import com.lde.usermicroservice.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDownloadService {

    private final DownloadRecordRepository downloadRecordRepository;
    private final UserRepository userRepository;
    private final DocumentClient documentClient;

    /**
     * Enregistre un nouvel événement de téléchargement avec enrichissement via Feign Client.
     */
    public void recordDownloadEvent(DownloadEventDTO eventDto) {
        log.info("Enregistrement d'un téléchargement pour userId: {} et examId: {}",
                eventDto.getUserId(), eventDto.getExamId());

        // Validation de l'existence de l'utilisateur
        Optional<User> userOpt = userRepository.findById(eventDto.getUserId());
        if (userOpt.isEmpty()) {
            log.error("Utilisateur non trouvé pour ID: {}", eventDto.getUserId());
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + eventDto.getUserId());
        }

        // Enrichissement des données via Feign Client
        DocumentDTO documentInfo = null;
        try {
            documentInfo = documentClient.getDocumentById(eventDto.getExamId());
            log.info("Document récupéré via Feign Client: {}", documentInfo.getTitle());
        } catch (Exception e) {
            log.warn("Impossible de récupérer les infos du document {} via Feign Client: {}",
                    eventDto.getExamId(), e.getMessage());
            // Utiliser les données de l'événement comme fallback
        }

        // Créer l'enregistrement avec les données enrichies ou de fallback
        DownloadRecord record = DownloadRecord.builder()
                .userId(Long.valueOf(eventDto.getUserId()))
                .examId(eventDto.getExamId())
                .examTitle(documentInfo != null ? documentInfo.getTitle() : eventDto.getExamTitle())
                .examType(documentInfo != null ? documentInfo.getType() : eventDto.getExamType())
                .downloadDate(eventDto.getDownloadDate() != null ? eventDto.getDownloadDate() : LocalDateTime.now())
                .build();

        downloadRecordRepository.save(record);
        log.info("DownloadRecord créé avec succès pour user ID: {} et exam ID: {}",
                eventDto.getUserId(), eventDto.getExamId());
    }

    /**
     * Récupère l'historique paginé des téléchargements avec enrichissement des données.
     */
    public Page<UserDownloadHistoryDTO> getDownloadHistoryForUser(Long userId, Pageable pageable) {
        log.info("Récupération de l'historique des téléchargements pour userId: {}", userId);

        // Validation de l'existence de l'utilisateur
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Utilisateur non trouvé avec l'id: {}", userId);
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + userId);
        }

        Page<DownloadRecord> page = downloadRecordRepository.findByUserId(userId, pageable);

        List<UserDownloadHistoryDTO> dtos = page.getContent().stream()
                .map(this::convertToUserDownloadHistoryDtoWithEnrichment)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * Récupère les statistiques de téléchargement d'un utilisateur.
     */
    public DownloadStatsDTO getDownloadStatsForUser(Long userId) {
        log.info("Calcul des statistiques de téléchargement pour userId: {}", userId);

        // Validation de l'existence de l'utilisateur
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Utilisateur non trouvé avec l'id: {}", userId);
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + userId);
        }

        // Total des téléchargements
        long totalDownloads = downloadRecordRepository.countByUserId(userId);

        // Téléchargements du mois courant
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        long monthlyDownloads = downloadRecordRepository.countByUserIdAndDownloadDateBetween(
                userId, monthStart, monthEnd);

        // Matière la plus téléchargée
        Optional<String> mostDownloadedSubjectOpt = downloadRecordRepository.findMostDownloadSubjectIDByUserId(userId);

        return DownloadStatsDTO.builder()
                .totalDownloads(totalDownloads)
                .monthlyDownloads(monthlyDownloads)
                .mostDownloadedSubject(mostDownloadedSubjectOpt.orElse("Aucune"))
                .build();
    }

    /**
     * Enrichit un DownloadRecord avec les données du service académique via Feign Client.
     */
    private UserDownloadHistoryDTO convertToUserDownloadHistoryDtoWithEnrichment(DownloadRecord record) {
        UserDownloadHistoryDTO.UserDownloadHistoryDTOBuilder dtoBuilder = UserDownloadHistoryDTO.builder()
                .recordId(record.getId())
                .examId(record.getExamId())
                .examTitle(record.getExamTitle())
                .examType(record.getExamType())
                .downloadDate(record.getDownloadDate());

        // Tentative d'enrichissement via Feign Client
        try {
            DocumentDTO documentInfo = documentClient.getDocumentById(record.getExamId());

            // Mise à jour avec les données fraîches du service académique
            dtoBuilder
                    .examTitle(documentInfo.getTitle())
                    .examType(documentInfo.getType());

            log.debug("Données enrichies via Feign Client pour examId: {}", record.getExamId());

        } catch (Exception e) {
            log.warn("Impossible d'enrichir les données pour examId {} via Feign Client: {}",
                    record.getExamId(), e.getMessage());
            // Utiliser les données locales comme fallback (déjà définies)
        }

        return dtoBuilder.build();
    }

    /**
     * Méthode utilitaire pour enrichir en lot les données de téléchargement (optimisation).
     */
    public List<UserDownloadHistoryDTO> enrichDownloadHistory(List<DownloadRecord> records) {
        return records.parallelStream()
                .map(this::convertToUserDownloadHistoryDtoWithEnrichment)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si un document existe via le service académique.
     */
    public boolean documentExists(String examId) {
        try {
            DocumentDTO document = documentClient.getDocumentById(examId);
            return document != null;
        } catch (Exception e) {
            log.warn("Document {} non trouvé dans le service académique: {}", examId, e.getMessage());
            return false;
        }
    }

    /**
     * Synchronise les données locales avec le service académique pour un utilisateur.
     */
    public void synchronizeUserDownloads(Long userId, Pageable pageable) {
        log.info("Synchronisation des téléchargements pour userId: {}", userId);

        Page<DownloadRecord> userDownloads = downloadRecordRepository.findByUserId(userId, pageable);

        for (DownloadRecord record : userDownloads) {
            try {
                DocumentDTO freshDocumentInfo = documentClient.getDocumentById(record.getExamId());

                // Mise à jour si les données ont changé
                boolean needsUpdate = false;
                if (!record.getExamTitle().equals(freshDocumentInfo.getTitle())) {
                    record.setExamTitle(freshDocumentInfo.getTitle());
                    needsUpdate = true;
                }
                if (!record.getExamType().equals(freshDocumentInfo.getType())) {
                    record.setExamType(freshDocumentInfo.getType());
                    needsUpdate = true;
                }

                if (needsUpdate) {
                    downloadRecordRepository.save(record);
                    log.info("Données synchronisées pour examId: {}", record.getExamId());
                }

            } catch (Exception e) {
                log.warn("Impossible de synchroniser examId {}: {}", record.getExamId(), e.getMessage());
            }
        }
    }
}