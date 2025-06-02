package com.lde.paymentmicroservice.services;

import com.lde.paymentmicroservice.models.Payment;
import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.PaymentRepository;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final WebClient webClient;
    private final OrangeMoneyConfig config;

    public Payment initiatePayment(UUID userId, BigDecimal amount) {
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(amount)
                .status("PENDING")
                .transactionRef(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .build();
        return paymentRepo.save(payment);
    }

    public void handleCallback(String transactionRef, boolean success) {
        Payment payment = paymentRepo.findByTransactionRef(transactionRef)
                .orElseThrow();
        payment.setStatus(success ? "SUCCESS" : "FAILED");
        paymentRepo.save(payment);

        if (success) {
            subscriptionRepo.save(
                    Subscription.builder()
                            .userId(payment.getUserId())
                            .startDate(Instant.now())
                            .endDate(Instant.now().plus(180, ChronoUnit.DAYS))
                            .status("ACTIVE")
                            .payment(payment)
                            .build()
            );
        }
    }
}
