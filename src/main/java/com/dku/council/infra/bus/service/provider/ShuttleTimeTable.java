package com.dku.council.infra.bus.service.provider;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShuttleTimeTable {
    private static final String TIME_TABLE_PATH = "/shuttle_time.table";
    private List<LocalTime> time;

    /**
     * 셔틀 시간표를 resources로부터 불러옵니다.
     */
    public void loadTimeTable() {
        time = new ArrayList<>();

        try (
                InputStream stream = ShuttleTimeTable.class.getResourceAsStream(TIME_TABLE_PATH);
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader bReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = bReader.readLine()) != null) {
                parseTableLine(line);
            }

            if (time.isEmpty()) {
                throw new RuntimeException("셔틀 시간표를 불러올 수 없습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseTableLine(String line) {
        line = line.trim();
        if (line.startsWith("#") || line.isBlank()) {
            return;
        }

        if (time.isEmpty()) {
            time.add(LocalTime.parse(line));
        } else {
            String[] token = line.substring(1).split("\\*");
            if (token.length != 2) {
                throw new IllegalArgumentException("*기호가 없거나 2개 이상입니다.");
            }

            int offset = Integer.parseInt(token[0]);
            int count = Integer.parseInt(token[1]);
            LocalTime prev = time.get(time.size() - 1);

            for (int i = 0; i < count; i++) {
                LocalTime next = prev.plusMinutes(offset);
                time.add(next);
                prev = next;
            }
        }
    }

    /**
     * 현재 시각을 기준으로 다음버스 도착까지 남은 시간을 계산합니다.
     *
     * @param now 현재 시각
     * @return 남은 시간
     */
    public Duration remainingNextBusArrival(LocalTime now) {
        if (time == null) {
            loadTimeTable();
        }

        LocalTime first = time.get(0);
        if (now.isBefore(first)) {
            return Duration.between(now, first);
        }

        LocalTime last = time.get(time.size() - 1);
        if (isSameInSeconds(now, last) || now.isAfter(last)) {
            return Duration.ofDays(1).minus(Duration.between(first, now));
        }

        int start = 0;
        int end = time.size() - 1;
        while (start < end) {
            int mid = start + (end - start) / 2;
            LocalTime rangeStart = time.get(mid);
            LocalTime rangeEnd = time.get(mid + 1);

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
