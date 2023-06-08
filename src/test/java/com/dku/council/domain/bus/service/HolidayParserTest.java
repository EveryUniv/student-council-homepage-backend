package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.holiday.model.Holiday;
import com.dku.council.domain.bus.holiday.service.HolidayParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.MonthDay;
import java.util.List;

import static com.dku.council.domain.bus.holiday.model.Holiday.SubstitutionType.*;
import static org.assertj.core.api.Assertions.assertThat;

class HolidayParserTest {

    private final HolidayParser parser = new HolidayParser();

    @Test
    @DisplayName("정상적인 휴일 파싱")
    void parse() {
        // when
        List<Holiday> actual = parser.parse("/holiday/holidays1");

        // then
        assertThat(actual).containsExactlyInAnyOrder(
                new Holiday(MonthDay.of(1, 1), NONE, false, false),
                new Holiday(MonthDay.of(3, 1), ALL, false, false),
                new Holiday(MonthDay.of(5, 5), ALL, false, false),
                new Holiday(MonthDay.of(6, 6), NONE, false, false),
                new Holiday(MonthDay.of(8, 15), ALL, false, false),
                new Holiday(MonthDay.of(10, 3), ALL, false, false),
                new Holiday(MonthDay.of(10, 9), ALL, false, false),
                new Holiday(MonthDay.of(12, 25), NONE, false, false),
                new Holiday(MonthDay.of(1, 1), ONLY_SUNDAY, true, true),
                new Holiday(MonthDay.of(1, 1), ALL, true, false),
                new Holiday(MonthDay.of(1, 2), ONLY_SUNDAY, true, false),
                new Holiday(MonthDay.of(4, 8), NONE, true, false),
                new Holiday(MonthDay.of(8, 14), ONLY_SUNDAY, true, false),
                new Holiday(MonthDay.of(8, 15), ALL, true, false),
                new Holiday(MonthDay.of(8, 16), ONLY_SUNDAY, true, false)
        );
    }
}