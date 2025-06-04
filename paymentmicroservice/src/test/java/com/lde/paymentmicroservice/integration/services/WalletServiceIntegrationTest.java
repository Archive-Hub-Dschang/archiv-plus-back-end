package com.lde.paymentmicroservice.integration.services;

import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.repositories.WalletRepository;
import com.lde.paymentmicroservice.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.UserDto;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class WalletServiceIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @MockBean
    private UserClient userClient;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
        when(userClient.getUserById(1L)).thenReturn(new UserDto(1L, "john", "john@example.com"));
    }

    @Test
    void credit_ShouldIncreaseWalletBalance() {
        Wallet wallet = walletService.credit(1L, 5000.0);
        assertEquals(18500.0 + 5000.0, wallet.getBalance());
    }

    @Test
    void debit_ShouldDecreaseWalletBalance() {
        walletService.credit(1L, 1000.0);
        Wallet wallet = walletService.debit(1L, 500.0);
        assertEquals(18500.0 + 1000.0 - 500.0, wallet.getBalance());
    }

    @Test
    void getBalance_ShouldReturnWalletBalance() {
        walletService.credit(1L, 0.0);
        Double balance = walletService.getBalance(1L);
        assertEquals(18500.0, balance);
    }

    @Test
    void debit_ShouldThrowException_WhenInsufficientBalance() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.debit(1L, 20000.0);
        });
        assertEquals("Solde insuffisant", exception.getMessage());
    }
}
