package com.lde.paymentmicroservice.dto;

import lombok.Data;

@Data
public class PaymentCallbackDTO {
    private String transactionRef;
    private boolean success;
}
