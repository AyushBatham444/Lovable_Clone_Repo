package com.project.demo.Service;

import com.project.demo.dto.subscription.CheckoutRequest;
import com.project.demo.dto.subscription.CheckoutResponse;
import com.project.demo.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse createCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
