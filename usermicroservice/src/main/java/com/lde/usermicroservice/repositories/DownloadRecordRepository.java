package com.lde.usermicroservice.repositories;

import com.lde.usermicroservice.models.DownloadRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long> {

    Page<DownloadRecord> findByUserId(Long userId, Pageable pageable);

    // Compter total téléchargements (nombre d'enregistrements)
    long countByUserId(Long userId);

    // Nombre d'examens uniques téléchargés par user
    @Query("SELECT COUNT(DISTINCT d.examId) FROM DownloadRecord d WHERE d.userId = :userId")
    Long countUniqueExamsByUserId(@Param("userId") long userId);

    // Dernière date de téléchargement
    @Query("SELECT MAX(d.downloadDate) FROM DownloadRecord d WHERE d.userId = :userId")
    LocalDateTime findLastDownloadedAtByUserId(@Param("userId") Long userId);

    // Nombre de téléchargements dans une période
    long countByUserIdAndDownloadDateBetween(Long userId, LocalDateTime monthStart, LocalDateTime monthEnd);

    // Examen le plus téléchargé (native SQL)
    @Query(value = "SELECT exam_id FROM download_records WHERE user_id = :userId GROUP BY exam_id ORDER BY COUNT(*) DESC LIMIT 1", nativeQuery = true)
    Optional<String> findMostDownloadSubjectIDByUserId(@Param("userId") Long userId);
}
