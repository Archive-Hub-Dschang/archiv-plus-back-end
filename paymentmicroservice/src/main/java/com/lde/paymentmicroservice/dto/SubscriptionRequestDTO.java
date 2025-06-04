package com.lde.paymentmicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
public class SubscriptionRequestDTO {
    @NotBlank(message = "L'Id de semestre est requis")
    private String semesterId;
    @NotBlank(message = "L'Id de l'utilisateur est requis")
    private Long userId;
    private Double amount;
    public SubscriptionRequestDTO(String semesterId, Long userId, Double amount) {
        this.semesterId = semesterId;
        this.userId = userId;
        this.amount = amount;
    }
}
