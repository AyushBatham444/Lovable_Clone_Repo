package com.project.demo.mapper;

import com.project.demo.Entity.Plan;
import com.project.demo.Entity.Subscription;
import com.project.demo.dto.subscription.PlanResponse;
import com.project.demo.dto.subscription.SubscriptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
