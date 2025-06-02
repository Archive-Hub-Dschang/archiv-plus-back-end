package com.lde.paymentmicroservice.services;

import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUserId(userId);
                    wallet.setBalance(0.0);
                    return walletRepository.save(wallet);
                });
    }

    public Wallet credit(Long userId, Double amount) {
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }

    public Wallet debit(Long userId, Double amount) {
        Wallet wallet = getOrCreateWallet(userId);
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        return walletRepository.save(wallet);
    }

    public Double getBalance(Long userId) {
        Wallet wallet = getOrCreateWallet(userId);
        return wallet.getBalance();
    }
}

