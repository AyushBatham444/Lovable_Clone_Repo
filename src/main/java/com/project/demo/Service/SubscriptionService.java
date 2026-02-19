package com.project.demo.Service;

import com.project.demo.Entity.enums.SubscriptionStatus;
import com.project.demo.dto.subscription.CheckoutRequest;
import com.project.demo.dto.subscription.CheckoutResponse;
import com.project.demo.dto.subscription.PortalResponse;
import com.project.demo.dto.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService {

    SubscriptionResponse getCurrentSubscription();


    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
