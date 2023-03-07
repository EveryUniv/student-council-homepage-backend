package com.dku.council.infra.bus.service.provider;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ShuttleTimeTableTest {

    private static ShuttleTimeTable table;

    @BeforeAll
    static void setup() {
        table = new ShuttleTimeTable();
        table.loadTimeTable();
    }

    @Test
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