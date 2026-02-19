package com.project.demo.repository;

import com.project.demo.Entity.Subscription;
import com.project.demo.Entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {

    // get the current active subscription
    Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet); // valid hibernate query will work on its own

    boolean existsByStripeSubscriptionId(String subscriptionId);

    Optional<Subscription> findByStripeSubscriptionId(String gatewaySubscriptionId); // // valid hibernate query will work on its own
}
