package com.lde.paymentmicroservice.integration.controllers;

import com.lde.paymentmicroservice.models.Wallet;
import com.lde.paymentmicroservice.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
        Wallet wallet = Wallet.builder()
                .userId(1L)
                .balance(10000.0)
                .build();
        walletRepository.save(wallet);
    }

    @Test
    void getBalance_ShouldReturnCorrectBalance() throws Exception {
        mockMvc.perform(get("/api/wallet/{userId}/balance", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("10000.0"));
    }

    @Test
    void credit_ShouldIncreaseBalance() throws Exception {
        mockMvc.perform(post("/api/wallet/{userId}/credit", 1L)
                        .param("amount", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(12000.0));
    }

    @Test
    void debit_ShouldDecreaseBalance() throws Exception {
        mockMvc.perform(post("/api/wallet/{userId}/debit", 1L)
                        .param("amount", "3000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(7000.0));
    }

}