package com.lde.paymentmicroservice.services;

import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // Créer un abonnement par semestre
    public Subscription createSubscription(Long userId, String semesterId) {
        Optional<Subscription> existing = subscriptionRepository.findByUserIdAndSemesterId(userId, semesterId);

        if (existing.isPresent()) {
            throw new IllegalStateException("L'utilisateur est déjà abonne à ce semestre.");
        }

        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setSemesterId(semesterId);
        subscription.setSubscriptionDate(LocalDate.now());
        subscription.setActive(true);


        return subscriptionRepository.save(subscription);
    }

    public Subscription deactivateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("abonnement introuvable."));
        subscription.setActive(false);
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }


}
