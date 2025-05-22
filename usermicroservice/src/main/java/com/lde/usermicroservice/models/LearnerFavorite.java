package com.lde.usermicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
public class LearnerFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long learnerId;         // ID de l’utilisateur
    private String subjectId;    // ID du sujet
    private LocalDateTime addedAt;

}
