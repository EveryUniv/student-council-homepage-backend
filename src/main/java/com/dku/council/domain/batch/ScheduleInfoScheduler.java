package com.dku.council.domain.batch;

import com.dku.council.domain.mainpage.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ScheduleInfoScheduler {

    private final Clock clock;
    private final ScheduleService service;

    @Scheduled(cron = "${dku.schedule.cron}")
    public void loadToDB() {
        LocalDate now = LocalDate.now(clock);
        service.loadSchedulesYear(now);
    }
}
