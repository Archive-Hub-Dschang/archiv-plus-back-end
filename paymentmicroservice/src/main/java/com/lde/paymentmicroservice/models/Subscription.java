package com.lde.paymentmicroservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String semesterId;
    private LocalDate subscriptionDate;
    private LocalDate endSubscriptionDate;
    //abonnement actif ou pas
    private boolean active;
    public Subscription() {
        // constructeur vide
    }
}

