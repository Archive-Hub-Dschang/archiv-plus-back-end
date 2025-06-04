package com.lde.paymentmicroservice.services;

import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.UserDto;
import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.repositories.WalletRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserClient userClient;

    @Autowired
    public WalletService(WalletRepository walletRepository, UserClient userClient) {
        this.walletRepository = walletRepository;
        this.userClient = userClient;
    }

    private UserDto fetchUser(Long userId) {
        try {
            return userClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Utilisateur D'id=" + userId + " non trouvé");
        }
    }

    public Wallet getOrCreateWallet(Long userId) {
        UserDto user = fetchUser(userId);
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setUserId(userId);
                    wallet.setBalance(18500.0);
                    return walletRepository.save(wallet);
                });
    }

    public Wallet credit(Long userId, Double amount) {
        UserDto user = fetchUser(userId);

        Wallet wallet = getOrCreateWallet(user.getId());
        wallet.setBalance(wallet.getBalance() + amount);
        return walletRepository.save(wallet);
    }

    public Wallet debit(Long userId, Double amount) {
        UserDto user = fetchUser(userId);

        Wallet wallet = getOrCreateWallet(user.getId());
        if (wallet.getBalance() < amount) {
            throw new IllegalArgumentException("Solde insuffisant");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        return walletRepository.save(wallet);
    }

    public Double getBalance(Long userId) {
        UserDto user = fetchUser(userId);

        Wallet wallet = getOrCreateWallet(user.getId());
        return wallet.getBalance();
    }
}

