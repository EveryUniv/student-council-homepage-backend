package com.dku.council.domain.schedule.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ScheduleDto {
    private final String title;
    private final LocalDate start;
    private final LocalDate end;

    public ScheduleDto(Schedule entity) {
        this.title = entity.getTitle();
        this.start = entity.getStartDate();
        this.end = entity.getEndDate();
    }
}
