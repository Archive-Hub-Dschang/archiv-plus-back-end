package com.lde.paymentmicroservice.unitaire.services;

import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.UserDto;
import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.repositories.WalletRepository;
import com.lde.paymentmicroservice.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceUnitaireTest {

    private WalletRepository walletRepository;
    private UserClient userClient;
    private WalletService walletService;

    @BeforeEach
    public void setup() {
        walletRepository = mock(WalletRepository.class);
        userClient = mock(UserClient.class);
        walletService = new WalletService(walletRepository, userClient);
    }

    @Test
    public void testGetOrCreateWallet_CreatesNewWalletIfNotExists() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        when(userClient.getUserById(userId)).thenReturn(userDto);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet wallet = walletService.getOrCreateWallet(userId);

        assertNotNull(wallet);
        assertEquals(userId, wallet.getUserId());
        assertEquals(18500.0, wallet.getBalance());
    }

    @Test
    public void testCredit_AddsAmountToWallet() {
        Long userId = 1L;
        Double initialBalance = 1000.0;
        Double creditAmount = 500.0;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        Wallet existingWallet = new Wallet();
        existingWallet.setUserId(userId);
        existingWallet.setBalance(initialBalance);

        when(userClient.getUserById(userId)).thenReturn(userDto);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet updatedWallet = walletService.credit(userId, creditAmount);

        assertEquals(initialBalance + creditAmount, updatedWallet.getBalance());
    }

    @Test
    public void testDebit_RemovesAmountFromWallet() {
        Long userId = 1L;
        Double initialBalance = 1000.0;
        Double debitAmount = 300.0;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(initialBalance);

        when(userClient.getUserById(userId)).thenReturn(userDto);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet updatedWallet = walletService.debit(userId, debitAmount);

        assertEquals(initialBalance - debitAmount, updatedWallet.getBalance());
    }

    @Test
    public void testDebit_ThrowsExceptionWhenInsufficientFunds() {
        Long userId = 1L;
        Double initialBalance = 100.0;
        Double debitAmount = 200.0;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(initialBalance);

        when(userClient.getUserById(userId)).thenReturn(userDto);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            walletService.debit(userId, debitAmount);
        });

        assertEquals("Solde insuffisant", exception.getMessage());
    }

    @Test
    public void testGetBalance_ReturnsWalletBalance() {
        Long userId = 1L;
        Double expectedBalance = 2000.0;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(expectedBalance);

        when(userClient.getUserById(userId)).thenReturn(userDto);
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        Double balance = walletService.getBalance(userId);

        assertEquals(expectedBalance, balance);
    }
}