package com.project.demo.Service;

import com.project.demo.dto.subscription.PlanLimitsResponse;
import com.project.demo.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
     void recordTokenUsage(Long userId,int actualTokens);
     void checkDailyTokensUsage();
}
