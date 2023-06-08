package com.dku.council.domain.bus.holiday.model;

import com.dku.council.global.util.DateUtil;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.MonthDay;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Holiday implements Comparable<Holiday> {
    /**
     * 공휴일 날짜
     */
    private final MonthDay day;

    /**
     * 대체공휴일 적용 여부
     */
    private final SubstitutionType substitutionType;

    /**
     * 음력인지?
     */
    private final boolean isLunar;

    /**
     * 1일 빼야하는지?
     */
    private final boolean isNextDay;


    public Holiday(MonthDay day, SubstitutionType substitutionType) {
        this.day = day;
        this.substitutionType = substitutionType;
        this.isLunar = false;
        this.isNextDay = false;
    }


    @Override
    public int compareTo(Holiday o) {
        return day.compareTo(o.day);
    }

    public LocalDate getDay(int year) {
        LocalDate date = day.atYear(year);
        if (isLunar) {
            date = DateUtil.toSolarDate(date);
        }
        if (isNextDay) {
            date = date.minusDays(1);
        }
        return date;
    }

    public SubstitutionType getSubstitutionType() {
        return substitutionType;
    }


    public enum SubstitutionType {
        NONE, ONLY_SUNDAY, ALL
    }
}
