package com.dku.council.global.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {
    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDate toSolarDate(LocalDate lunarDate) {
        KoreanLunarCalendar calendar = new KoreanLunarCalendar();
        calendar.setLunarDate(lunarDate, false);
        return calendar.getSolarDate();
    }
}
