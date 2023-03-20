package com.dku.council.mock;

import com.dku.council.domain.mainpage.model.entity.Schedule;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class ScheduleMock {
    public static List<Schedule> createList(int size, LocalDate start, Period gap) {
        List<Schedule> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            LocalDate startDate = start.plus(gap);
            Schedule schedule = Schedule.builder()
                    .title("title" + i)
                    .startDate(startDate)
                    .endDate(startDate.plus(gap))
                    .build();
            result.add(schedule);
            start = startDate.plus(gap);
        }
        return result;
    }
}
