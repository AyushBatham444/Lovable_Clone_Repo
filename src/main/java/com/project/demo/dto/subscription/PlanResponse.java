package com.project.demo.dto.subscription;

import com.project.demo.Entity.enums.SubscriptionStatus;

public record PlanResponse(
        Long id,
        String name,
        Integer maxProjects,
        Integer maxTokensPerDay,
        Boolean unlimitedAi,
        String price
) {
}
