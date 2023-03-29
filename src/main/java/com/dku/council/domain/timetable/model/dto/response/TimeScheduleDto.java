package com.dku.council.domain.timetable.model.dto.response;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class TimeScheduleDto {

    private final String name;
    private final String memo;
    private final String color;
    private final List<TimePromise> times;


    public TimeScheduleDto(ObjectMapper mapper, TimeSchedule schedule) {
        this.name = schedule.getName();
        this.memo = schedule.getMemo();
        this.color = schedule.getColor();
        this.times = TimePromise.parse(mapper, schedule.getTimesJson());
    }
}
