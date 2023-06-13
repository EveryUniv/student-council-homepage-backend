package com.dku.council.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DateUtilTest {

    @Test
    @DisplayName("LocalDateTime을 Instant로 변환")
    void toInstant() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2022, 8, 1, 13, 10, 10);

        // when
        Instant instant = DateUtil.toInstant(localDateTime);

        // then
        ZonedDateTime actual = instant.atZone(ZoneId.systemDefault());
        ZonedDateTime expected = ZonedDateTime.of(2022, 8, 1, 13, 10, 10, 0, ZoneId.systemDefault());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("음력을 양력으로 변환")
    void toSolarDate() {
        // given
        List<LocalDate> lunarDates = List.of(
                LocalDate.of(2020, 1, 3),
                LocalDate.of(2021, 5, 15),
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 3, 1),
                LocalDate.of(2022, 4, 29),
                LocalDate.of(2022, 6, 15),
                LocalDate.of(2022, 7, 29),
                LocalDate.of(2022, 12, 1)
        );

        // when
        List<LocalDate> solarDates = lunarDates.stream()
                .map(DateUtil::toSolarDate)
                .collect(Collectors.toList());

        // then
        LocalDate[] expected = {
                LocalDate.of(2020, 1, 27),
                LocalDate.of(2021, 6, 24),
                LocalDate.of(2022, 2, 1),
                LocalDate.of(2022, 4, 1),
                LocalDate.of(2022, 5, 29),
                LocalDate.of(2022, 7, 13),
                LocalDate.of(2022, 8, 26),
                LocalDate.of(2022, 12, 23)
        };
        assertThat(solarDates).containsExactly(expected);
    }
}