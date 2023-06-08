package com.dku.council.domain.bus.holiday.service;

import com.dku.council.domain.bus.holiday.exception.CannotGetHolidays;
import com.dku.council.domain.bus.holiday.model.Holiday;
import com.dku.council.infra.bus.predict.impl.TimeTable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class HolidayParser {

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("M-d");
    private static final String ALL_FLAGS = "LASP";

    private Set<Character> flags;
    private List<Holiday> holidays;

    /**
     * 휴일 정보를 읽어옵니다.
     */
    public List<Holiday> parse(String path) {
        flags = new HashSet<>();
        holidays = new ArrayList<>();

        try (
                InputStream stream = TimeTable.class.getResourceAsStream(path);
                InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                BufferedReader bReader = new BufferedReader(reader)
        ) {
            String line;
            while ((line = bReader.readLine()) != null) {
                parseLine(line);
            }

            return holidays;
        } catch (IOException | NullPointerException e) {
            throw new CannotGetHolidays(e);
        }
    }

    private void parseLine(String line) {
        line = line.trim();

        int commentIdx = line.indexOf('#');
        if (commentIdx != -1) {
            line = line.substring(0, commentIdx).trim();
        }

        if (line.isBlank()) {
            return;
        }

        int idx = parseOption(line);
        if (idx == line.length()) {
            throw new CannotGetHolidays("날짜 정보가 없습니다: " + line);
        }

        MonthDay monthDay = parseDate(line, idx);
        holidays.add(buildHoliday(monthDay));
        flags.clear();
    }

    private int parseOption(String line) {
        int idx = 0;
        while (idx < line.length() && !Character.isDigit(line.charAt(idx))) {
            char c = Character.toUpperCase(line.charAt(idx));
            if (ALL_FLAGS.contains(String.valueOf(c))) {
                flags.add(c);
            } else {
                throw new CannotGetHolidays("알 수 없는 옵션입니다 - " + c + ": " + line);
            }
            idx++;
        }
        return idx;
    }

    private Holiday buildHoliday(MonthDay day) {
        boolean isLunar = flags.contains('L');
        boolean isNextDay = flags.contains('P');
        Holiday.SubstitutionType type = Holiday.SubstitutionType.NONE;

        if (flags.contains('S')) {
            type = Holiday.SubstitutionType.ALL;
        } else if (flags.contains('A')) {
            type = Holiday.SubstitutionType.ONLY_SUNDAY;
        }

        return new Holiday(day, type, isLunar, isNextDay);
    }

    private MonthDay parseDate(String line, int idx) {
        try {
            return MonthDay.parse(line.substring(idx), LOCAL_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CannotGetHolidays("날짜 정보를 읽을 수 없습니다: " + line);
        }
    }
}
