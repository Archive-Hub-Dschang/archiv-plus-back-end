package com.lde.paymentmicroservice.services;

import com.lde.paymentmicroservice.clients.SemesterClient;
import com.lde.academicservice.dto.SemesterDTO;
import com.lde.paymentmicroservice.clients.UserClient;
import com.lde.paymentmicroservice.dto.SubscriptionRequestDTO;
import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service

public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SemesterClient semesterClient;
    private final UserClient userClient ;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SemesterClient semesterClient, UserClient userClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.semesterClient = semesterClient;
        this.userClient = userClient;
    }

    public Subscription createSubscription(SubscriptionRequestDTO request) {
        if (request.getSemesterId() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Semester ID and User ID must not be null");
        }
        if (userClient.getUserById(request.getUserId()) == null) {
            throw new EntityNotFoundException("utilisateur inexitant" + request.getUserId());
        }

        SemesterDTO semester = semesterClient.getSemesterById(request.getSemesterId());

        Subscription subscription = new Subscription();
        subscription.setUserId(request.getUserId());
        subscription.setSemesterId(request.getSemesterId());
        subscription.setSubscriptionDate(LocalDate.now());
        subscription.setEndSubscriptionDate(semester.getEndSemesterDate());
        subscription.setActive(true);

        return subscriptionRepository.save(subscription);
    }
    public Subscription deactivateSubscription(Long id) {
        // Vérifier si l'abonnement existe
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Abonnement non trouvé"));
        SemesterDTO semester = semesterClient.getSemesterById(subscription.getSemesterId());

        if (subscription.getEndSubscriptionDate().equals(semester.getEndSemesterDate())) {
            subscription.setActive(false);
        }
        return subscriptionRepository.save(subscription);
    }
}
