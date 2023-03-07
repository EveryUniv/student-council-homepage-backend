package com.dku.council.util;

import java.time.*;

public class ClockUtil {
    public static Clock create() {
        return create(1_000_000_000);
    }

    public static Clock create(LocalDateTime datetime) {
        return create(datetime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    public static Clock create(LocalTime time) {
        return create(LocalDate.of(2022, 1, 1)
                .atTime(time)
                .atZone(ZoneId.systemDefault())
                .toEpochSecond());
    }

    public static Clock create(long epochSeconds) {
        return Clock.fixed(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
    }
}
