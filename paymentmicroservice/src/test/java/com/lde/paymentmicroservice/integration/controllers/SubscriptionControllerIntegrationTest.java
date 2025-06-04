package com.lde.paymentmicroservice.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lde.paymentmicroservice.clients.SemesterClient;
import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.SemesterDTO;
import com.lde.paymentmicroservice.dto.SubscriptionRequestDTO;
import com.lde.paymentmicroservice.dto.UserDto;
import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import com.lde.paymentmicroservice.services.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @MockBean
    private SemesterClient semesterClient;

    @MockBean
    private WalletService walletService;

    @Test
    void testSubscribe_Success() throws Exception {
        SubscriptionRequestDTO request = new SubscriptionRequestDTO("SEMESTER-1", 1L, 5000.0);

        when(userClient.getUserById(1L)).thenReturn(new UserDto()); // simulé valide
        when(semesterClient.getSemesterById("SEMESTER-1"))
                .thenReturn(new SemesterDTO("SEMESTER-1", LocalDate.now().plusMonths(4)));

        mockMvc.perform(post("/api/subscriptions/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.semesterId").value("SEMESTER-1"))
                .andExpect(jsonPath("$.amount").value(5000.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void deactivateSubscription_ShouldDeactivate_WhenSubscriptionExists() throws Exception {
        subscriptionRepository.deleteAll();

        Subscription subscription = new Subscription();
        subscription.setUserId(1L);
        subscription.setSemesterId("SEMESTER-1");
        subscription.setSubscriptionDate(LocalDate.now());
        subscription.setEndSubscriptionDate(LocalDate.now().plusMonths(4));
        subscription.setAmount(5000.0);
        subscription.setActive(true);
        subscription = subscriptionRepository.save(subscription);

        when(semesterClient.getSemesterById("SEMESTER-1"))
                .thenReturn(new SemesterDTO("SEMESTER-1", subscription.getEndSubscriptionDate()));

        mockMvc.perform(post("/api/subscriptions/" + subscription.getId() + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
    @Test
    void deactivateSubscription_ShouldReturnNotFound_WhenSubscriptionNotExists() throws Exception {
        mockMvc.perform(post("/api/subscriptions/{id}/deactivate", 9999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateSubscription_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/subscriptions/{id}/deactivate", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}