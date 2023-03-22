package com.dku.council.infra.bus.predict.impl;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class TimeTable {
    private final List<LocalTime> timeTables;

    public TimeTable(List<LocalTime> timeTables) {
        this.timeTables = timeTables;
    }

    /**
     * 현재 시각을 기준으로 다음버스 도착까지 남은 시간을 계산합니다.
     *
     * @param now 현재 시각
     * @return 남은 시간
     */
    public Duration remainingNextBusArrival(LocalTime now) {
        LocalTime first = timeTables.get(0);
        if (now.isBefore(first)) {
            return Duration.between(now, first);
        }

        LocalTime last = timeTables.get(timeTables.size() - 1);
        if (isSameInSeconds(now, last) || now.isAfter(last)) {
            return Duration.ofDays(1).minus(Duration.between(first, now));
        }

        int start = 0;
        int end = timeTables.size() - 1;
        while (start < end) {
            int mid = start + (end - start) / 2;
            LocalTime rangeStart = timeTables.get(mid);
            LocalTime rangeEnd = timeTables.get(mid + 1);

            if (isInRange(rangeStart, rangeEnd, now)) {
                return Duration.between(now, rangeEnd);
            } else if (now.isBefore(rangeStart)) {
                end = mid;
            } else {
                start = mid;
            }
        }
        return Duration.ofMinutes(0);
    }

    /**
     * 첫차 시각을 가져옵니다.
     */
    public LocalTime getFirstTime() {
        return timeTables.get(0);
    }

    /**
     * 막차 시각을 가져옵니다.
     */
    public LocalTime getLastTime() {
        return timeTables.get(timeTables.size() - 1);
    }

    private boolean isSameInSeconds(LocalTime a, LocalTime b) {
        return a.toSecondOfDay() == b.toSecondOfDay();
    }

    /**
     * 특정 시각이 range에 존재하는지 판단
     *
     * @param start (inclusive) start
     * @param end   (exclusive) end
     * @param now   대상 시각
     */
    private boolean isInRange(LocalTime start, LocalTime end, LocalTime now) {
        return isSameInSeconds(start, now) || (now.isAfter(start) && now.isBefore(end));
    }
}
