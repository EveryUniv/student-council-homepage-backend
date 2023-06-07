package com.dku.council.domain.bus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayParser holidayParser;
    private Set<MonthDay> holidays = null;


    public boolean isHoliday(LocalDate localDate) {
        if (holidays == null) {
            loadHolidays();
        }
        return holidays.contains(MonthDay.from(localDate));
    }

    private void loadHolidays() {
        holidays = holidayParser.parse();
    }
}
