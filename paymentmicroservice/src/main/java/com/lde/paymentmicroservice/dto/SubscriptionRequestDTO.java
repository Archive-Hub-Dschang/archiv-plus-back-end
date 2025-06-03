package com.lde.paymentmicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
@Data
public class SubscriptionRequestDTO {
    private String semesterId;
    private Long userId;
    private LocalDate subscriptionDate;

    public SubscriptionRequestDTO(String semesterId, Long userId, LocalDate subscriptionDate) {
        this.semesterId = semesterId;
        this.userId = userId;
        this.subscriptionDate = subscriptionDate;
    }
}
