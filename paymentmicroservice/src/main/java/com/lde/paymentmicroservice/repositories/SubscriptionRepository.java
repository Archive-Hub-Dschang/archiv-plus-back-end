package com.lde.paymentmicroservice.repositories;

import com.lde.paymentmicroservice.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    Optional<Subscription> findByUserIdAndSemesterId(Long userId, String semesterId);
    List<Subscription> findByUserId(Long userId);
}
