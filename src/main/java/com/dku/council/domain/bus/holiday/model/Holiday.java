package com.dku.council.domain.bus.holiday.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.MonthDay;

@Getter
@RequiredArgsConstructor
public class Holiday implements Comparable<Holiday> {
    /**
     * 공휴일 날짜
     */
    private final MonthDay day;

    /**
     * 대체공휴일 적용 여부
     */
    private final SubstitutionType substitutionType;

    @Override
    public int compareTo(Holiday o) {
        return day.compareTo(o.day);
    }

    public enum SubstitutionType {
        NONE, ONLY_SUNDAY, ALL
    }
}
