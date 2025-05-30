package com.lde.paymentmicroservice.controllers;

import com.lde.paymentmicroservice.models.Subscription;
import com.lde.paymentmicroservice.repositories.SubscriptionRepository;
import com.lde.paymentmicroservice.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Subscription> subscribe(
            @RequestParam Long userId,
            @RequestParam String semesterId
    ) {
        Subscription subscription = subscriptionService.createSubscription(userId, semesterId);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Subscription> deactivate(@PathVariable Long id) {
        Subscription subscription = subscriptionService.deactivateSubscription(id);
        return ResponseEntity.ok(subscription);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getUserSubscriptions(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }

}
