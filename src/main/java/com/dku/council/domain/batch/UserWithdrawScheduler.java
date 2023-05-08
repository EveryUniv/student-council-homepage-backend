package com.dku.council.domain.batch;

import com.dku.council.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UserWithdrawScheduler {

    private final UserService userService;

    @Value("${app.user.delete-period}")
    private final Duration deletePeriod;

    @Scheduled(cron = "* * * * * *")
    public void updateInactiveUsersToDefault() {
        LocalDateTime inactiveDate = LocalDateTime.now().minus(deletePeriod);
        userService.deleteInactiveUsers(inactiveDate);
        
    }
}
