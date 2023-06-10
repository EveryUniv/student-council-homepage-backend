package com.dku.council.domain.bus.holiday.service;

import com.dku.council.domain.bus.holiday.model.Holiday;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dku.council.domain.bus.holiday.model.Holiday.SubstitutionType.NONE;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayParser holidayParser;
    private List<Holiday> holidays = null;


    /**
     * 법정공휴일(일요일 + 외에 지정된 법정공휴일)인지 확인한다.
     *
     * @param localDate 확인할 날짜
     * @return 휴일이면 true, 아니면 false
     */
    public boolean isHoliday(LocalDate localDate) {
        if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return true;
        }

        return getHolidays(localDate.getYear()).contains(localDate);
    }

    /**
     * year년 모든 법정공휴일을 계산합니다. (공휴일아닌 일요일 제외)
     *
     * @param year 연도
     * @return 모든 법정공휴일
     */
    public Set<LocalDate> getHolidays(int year) {
        if (holidays == null) {
            holidays = holidayParser.parse("/holidays");
            Collections.sort(holidays);
        }

        Set<LocalDate> holidaySet = new HashSet<>();
        for (Holiday holiday : holidays) {
            LocalDate day = holiday.getDay(year);
            holidaySet.add(day);
        }

        for (Holiday holiday : holidays) {
            Holiday.SubstitutionType type = holiday.getSubstitutionType();
            if (type != NONE) {
                LocalDate day = holiday.getDay(year);
                holidaySet.add(getSubstituteDay(holidaySet, type, day));

                // 작년의 휴일이 대체공휴일로 연장되어 올해 휴일에 영향을 줄 수 있다.
                // 이런 현상으로 인해 올해 휴일이 누락되는 현상 방지
                // 2023년 법률기준 이런 현상이 발생할 수 없지만, 향후 확장성을 위해 작성
                LocalDate lastYearDay = getSubstituteDay(holidaySet, type, day.minusYears(1));
                if (lastYearDay.getYear() == year) {
                    holidaySet.add(lastYearDay);
                }
            }
        }

        return holidaySet;
    }

    /**
     * 대체공휴일을 적용한 날짜 계산
     *
     * @param holidaySet 휴일 목록
     * @param date       대체공휴일을 적용할 날짜
     * @return 대체공휴일이 적용된 날짜. 해당되지 않는다면 그대로 반환
     */
    private static LocalDate getSubstituteDay(Set<LocalDate> holidaySet, Holiday.SubstitutionType type, LocalDate date) {
        LocalDate newDate = date;
        while (isWeekend(type, newDate) || (date != newDate && holidaySet.contains(newDate))) {
            newDate = newDate.plusDays(1);
        }
        return newDate;
    }

    private static boolean isWeekend(Holiday.SubstitutionType type, LocalDate date) {
        DayOfWeek week = date.getDayOfWeek();
        if (type == Holiday.SubstitutionType.ALL && week == DayOfWeek.SATURDAY) {
            return true;
        }
        return week == DayOfWeek.SUNDAY;
    }
}
