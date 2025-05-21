package com.lde.paymentmicroservice.repositories;

import com.lde.paymentmicroservice.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
}
