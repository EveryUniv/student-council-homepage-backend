package com.dku.council.domain.bus.holiday.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.MonthDay;

import static com.dku.council.domain.bus.holiday.model.Holiday.SubstitutionType.NONE;
import static org.assertj.core.api.Assertions.assertThat;

class HolidayTest {

    @Test
    @DisplayName("아무런 옵션 없이 날짜 출력")
    void getDayNoOptions() {
        // given
        Holiday holiday = new Holiday(MonthDay.of(3, 1), NONE, false, false);

        // when
        LocalDate day = holiday.getDay(2022);

        // then
        assertThat(day).isEqualTo(LocalDate.of(2022, 3, 1));
    }

    @Test
    @DisplayName("음력 날짜 출력")
    void getDayLunar() {
        // given
        Holiday holiday = new Holiday(MonthDay.of(3, 1), NONE, true, false);

        // when
        LocalDate day = holiday.getDay(2022);

        // then
        assertThat(day).isEqualTo(LocalDate.of(2022, 4, 1));
    }

    @Test
    @DisplayName("이전 날짜 출력")
    void getDayNextDay() {
        // given
        Holiday holiday = new Holiday(MonthDay.of(3, 2), NONE, false, true);

        // when
        LocalDate day = holiday.getDay(2022);

        // then
        assertThat(day).isEqualTo(LocalDate.of(2022, 3, 1));
    }

    @Test
    @DisplayName("음력 & 이전 날짜 출력")
    void getDayLunarAndNextDay() {
        // given
        Holiday holiday = new Holiday(MonthDay.of(3, 1), NONE, true, true);

        // when
        LocalDate day = holiday.getDay(2022);

        // then
        assertThat(day).isEqualTo(LocalDate.of(2022, 3, 31));
    }
}