package com.dku.council.domain.mainpage.model.dto.response;

import com.dku.council.domain.mainpage.model.entity.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ScheduleResponseDto {
    private final String title;
    private final LocalDate start;
    private final LocalDate end;

    public ScheduleResponseDto(Schedule entity) {
        this.title = entity.getTitle();
        this.start = entity.getStartDate();
        this.end = entity.getEndDate();
    }
}
