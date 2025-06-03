package com.lde.paymentmicroservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDTO {
    private UUID userId;
    private BigDecimal amount;
}
