package com.dku.council.domain.batch;

import com.dku.council.domain.timetable.service.LectureRetrieveService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class LectureDumpScheduler {

    private final Clock clock;
    private final LectureRetrieveService service;

    @Scheduled(cron = "${dku.lecture.cron}")
    public void loadToDB() {
        YearMonth now = YearMonth.now(clock);
        service.reloadLectures(now);
    }
}
