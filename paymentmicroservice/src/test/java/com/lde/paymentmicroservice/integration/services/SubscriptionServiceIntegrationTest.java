package com.lde.paymentmicroservice.integration.services;

import com.lde.paymentmicroservice.clients.SemesterClient;
import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.SemesterDTO;
import com.lde.paymentmicroservice.dto.SubscriptionRequestDTO;
import com.lde.paymentmicroservice.dto.UserDto;
import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import com.lde.paymentmicroservice.services.SubscriptionService;
import com.lde.paymentmicroservice.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SubscriptionServiceIntegrationTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private UserClient userClient;

    @MockBean
    private SemesterClient semesterClient;

    @MockBean
    private WalletService walletService;

    private SubscriptionRequestDTO requestDTO;
    private SemesterDTO semesterDTO;

    @BeforeEach
    void setup() {
        subscriptionRepository.deleteAll();

        requestDTO = new SubscriptionRequestDTO("SEMESTER-1", 1L, 5000.0);
        semesterDTO = new SemesterDTO();
        semesterDTO.setId("SEMESTER-1");
        semesterDTO.setEndSemesterDate(LocalDate.now().plusMonths(3));

        when(userClient.getUserById(1L)).thenReturn(new UserDto());
        when(semesterClient.getSemesterById("SEMESTER-1")).thenReturn(semesterDTO);
    }

    @Test
    void createSubscription_ShouldSucceed_WhenDataIsValid() {
        Subscription subscription = subscriptionService.createSubscription(requestDTO);

        assertThat(subscription).isNotNull();
        assertThat(subscription.getUserId()).isEqualTo(1L);
        assertThat(subscription.getSemesterId()).isEqualTo("SEMESTER-1");
        assertThat(subscription.isActive()).isTrue();

        verify(walletService, times(1)).debit(1L, 5000.0);
        assertThat(subscriptionRepository.findById(subscription.getId())).isPresent();
    }

    @Test
    void createSubscription_ShouldThrow_WhenUserNotFound() {
        when(userClient.getUserById(2L)).thenReturn(null);
        SubscriptionRequestDTO invalidRequest = new SubscriptionRequestDTO("SEMESTER-1", 2L, 5000.0);

        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.createSubscription(invalidRequest);
        });
    }

    @Test
    void deactivateSubscription_ShouldSucceed_WhenValidId() {
        // D'abord, créer un abonnement actif
        Subscription subscription = subscriptionService.createSubscription(requestDTO);

        when(semesterClient.getSemesterById("SEMESTER-1")).thenReturn(semesterDTO);

        Subscription updated = subscriptionService.deactivateSubscription(subscription.getId());

        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void deactivateSubscription_ShouldThrow_WhenIdNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.deactivateSubscription(-1L);
        });
    }
}