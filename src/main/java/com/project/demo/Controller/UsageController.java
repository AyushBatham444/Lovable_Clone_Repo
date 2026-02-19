package com.project.demo.Controller;

import com.project.demo.Service.UsageService;
import com.project.demo.dto.subscription.PlanLimitsResponse;
import com.project.demo.dto.subscription.UsageTodayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageService usageServiceObj;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage(){
        Long userId=1L;
      //  return ResponseEntity.ok(usageServiceObj.getTodayUsageOfUser(userId));
        return null;
    }
}
