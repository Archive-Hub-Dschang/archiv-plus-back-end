package com.lde.usermicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearnerFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long learnerId;         // ID de l’utilisateur
    private String subjectId;    // ID du sujet
    private LocalDateTime addedAt;

}
