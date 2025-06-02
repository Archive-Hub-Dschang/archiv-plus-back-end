package com.lde.paymentmicroservice.controllers;

import com.lde.paymentmicroservice.models.Payment;
import com.lde.paymentmicroservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiate(@RequestBody PaymentRequestDTO request) {
        Payment payment = paymentService.initiatePayment(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody PaymentCallbackDTO callback) {
        paymentService.handleCallback(callback.getTransactionRef(), callback.isSuccess());
        return ResponseEntity.ok("Callback processed");
    }
}
