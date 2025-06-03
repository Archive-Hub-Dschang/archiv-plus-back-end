package com.lde.paymentmicroservice.controllers;

import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<Wallet> credit(@PathVariable Long userId, @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.credit(userId, amount));
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<Wallet> debit(@PathVariable Long userId, @RequestParam Double amount) {
        return ResponseEntity.ok(walletService.debit(userId, amount));
    }
}

