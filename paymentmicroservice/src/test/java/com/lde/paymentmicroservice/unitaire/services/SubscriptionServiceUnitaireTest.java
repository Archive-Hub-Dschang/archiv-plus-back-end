package com.lde.paymentmicroservice.unitaire.services;

import com.lde.paymentmicroservice.clients.SemesterClient;
import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.SemesterDTO;
import com.lde.paymentmicroservice.dto.SubscriptionRequestDTO;
import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import com.lde.paymentmicroservice.services.SubscriptionService;
import com.lde.paymentmicroservice.services.WalletService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubscriptionServiceUnitaireTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SemesterClient semesterClient;

    @Mock
    private UserClient userClient;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createSubscription_ShouldThrowException_WhenUserNotFound() {
        Long userId = 1L;
        String semesterId = "SEMESTER-1";

        when(userClient.getUserById(userId)).thenReturn(null);

        SubscriptionRequestDTO request = new SubscriptionRequestDTO(semesterId, userId,5000.0);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.createSubscription(request));
    }
    @Test
    void createSubscription_ShouldThrowException_WhenSemesterNotFound() {
        Long userId = 1L;
        String semesterId = "SEMESTER-1";

        when(semesterClient.getSemesterById(semesterId)).thenReturn(null);

        SubscriptionRequestDTO request = new SubscriptionRequestDTO(semesterId, userId,5000.0);

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.createSubscription(request));
    }

    @Test
    void deactivateSubscription_ShouldDeactivate_WhenEndDateReached() {
        Long subscriptionId = 1L;
        String semesterId = "SEMESTER-1";
        LocalDate endDate = LocalDate.now();

        Subscription subscription = new Subscription(subscriptionId, 10L, semesterId,
                LocalDate.now().minusMonths(5), endDate, 5000.0, true);

        SemesterDTO semesterDTO = new SemesterDTO(semesterId, endDate);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(semesterClient.getSemesterById(semesterId)).thenReturn(semesterDTO);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subscription result = subscriptionService.deactivateSubscription(subscriptionId);

        assertFalse(result.isActive());
    }

    @Test
    void deactivateSubscription_ShouldThrowException_WhenNotFound() {
        Long subscriptionId = 100L;
        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> subscriptionService.deactivateSubscription(subscriptionId));
    }
}
