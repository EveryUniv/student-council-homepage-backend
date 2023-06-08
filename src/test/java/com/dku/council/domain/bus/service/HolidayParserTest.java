package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.holiday.exception.CannotGetHolidays;
import com.dku.council.domain.bus.holiday.model.Holiday;
import com.dku.council.domain.bus.holiday.service.HolidayParser;
import org.junit.jupiter.api.Assertions;
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
        List<Holiday> actual = parser.parse("/holiday/holidays-success");

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

    @Test
    @DisplayName("Date를 잘못 입력한 경우 (포맷은 맞지만 존재하지 않는 날짜)")
    void failedInvalidDate() {
        // when & then
        Assertions.assertThrows(CannotGetHolidays.class, () ->
                parser.parse("/holiday/holidays-date-invalid-fail"));
    }

    @Test
    @DisplayName("Date를 틀린 포맷으로 표기한 경우")
    void failedWrongFormatDate() {
        // when & then
        Assertions.assertThrows(CannotGetHolidays.class, () ->
                parser.parse("/holiday/holidays-date-wrong-fail"));
    }

    @Test
    @DisplayName("Date를 표기하지 않은 경우")
    void failedEmptyDate() {
        // when & then
        Assertions.assertThrows(CannotGetHolidays.class, () ->
                parser.parse("/holiday/holidays-date-empty-fail"));
    }

    @Test
    @DisplayName("알 수 없는 옵션을 기입한 경우")
    void failedWrongOption() {
        // when & then
        Assertions.assertThrows(CannotGetHolidays.class, () ->
                parser.parse("/holiday/holidays-option-fail"));
    }
}