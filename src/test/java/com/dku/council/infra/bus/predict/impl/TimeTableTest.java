package com.dku.council.infra.bus.predict.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TimeTableTest {

    private static TimeTable table;

    @BeforeAll
    static void setup() {
        List<LocalTime> times = new ArrayList<>();
        times.add(LocalTime.of(10, 0));
        times.addAll(timePeriods(LocalTime.of(10, 0), 15, 4));
        times.addAll(timePeriods(LocalTime.of(11, 0), 10, 3));
        times.addAll(timePeriods(LocalTime.of(11, 30), 15, 10));
        times.addAll(timePeriods(LocalTime.of(14, 0), 10, 3));
        times.addAll(timePeriods(LocalTime.of(14, 30), 15, 6));
        times.addAll(timePeriods(LocalTime.of(16, 0), 10, 3));
        times.addAll(timePeriods(LocalTime.of(16, 30), 15, 10));
        times.addAll(timePeriods(LocalTime.of(19, 0), 20, 6));

        table = new TimeTable(times);
    }

    private static List<LocalTime> timePeriods(LocalTime start, int period, int count) {
        List<LocalTime> times = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            start = start.plusMinutes(period);
            times.add(start);
        }
        return times;
    }

    @Test
    @DisplayName("다음 버스 남은 시간을 잘 예측하는가")
    void remainingNextBusArrival() {
        // given
        List<LocalTime> testTimes = List.of(
                LocalTime.of(6, 0),
                LocalTime.of(9, 59),
                LocalTime.of(10, 0),
                LocalTime.of(10, 20),
                LocalTime.of(14, 25),
                LocalTime.of(18, 15),
                LocalTime.of(19, 10),
                LocalTime.of(20, 58),
                LocalTime.of(20, 59),
                LocalTime.of(21, 0),
                LocalTime.of(21, 1)
        );
        List<Duration> expectedTimes = List.of(
                Duration.ofHours(4),
                Duration.ofMinutes(1),
                Duration.ofMinutes(15),
                Duration.ofMinutes(10),
                Duration.ofMinutes(5),
                Duration.ofMinutes(15),
                Duration.ofMinutes(10),
                Duration.ofMinutes(2),
                Duration.ofMinutes(1),
                Duration.ofHours(13),
                Duration.ofMinutes(12 * 60 + 59)
        );

        // when
        List<Duration> results = testTimes.stream()
                .map(t -> table.remainingNextBusArrival(t))
                .collect(Collectors.toList());

        // then
        assertThat(results).containsExactlyElementsOf(expectedTimes);
    }
}