package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.holiday.model.Holiday;
import com.dku.council.domain.bus.holiday.service.HolidayParser;
import com.dku.council.domain.bus.holiday.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.dku.council.domain.bus.holiday.model.Holiday.SubstitutionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayParser parser;

    @InjectMocks
    private HolidayService service;


    @Test
    @DisplayName("대체공휴일 연휴 처리 확인")
    void isSeqSubstitutionHoliday() {
        // given
        List<Holiday> holidays = List.of(
                new Holiday(MonthDay.of(1, 1), ONLY_SUNDAY),
                new Holiday(MonthDay.of(1, 2), ALL),
                new Holiday(MonthDay.of(1, 3), ONLY_SUNDAY)
        );

        // when
        List<LocalDate> testSet = List.of(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 1, 2),
                LocalDate.of(2022, 1, 3),
                LocalDate.of(2022, 1, 4),
                LocalDate.of(2022, 1, 5),

                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 2),
                LocalDate.of(2023, 1, 3),
                LocalDate.of(2023, 1, 4),
                LocalDate.of(2023, 1, 5)
        );

        // then
        test(holidays, testSet, true, true, true, true, false, true, true, true, true, false);
    }

    @Test
    @DisplayName("단일 대체공휴일 처리 확인")
    void isSubstitutionHoliday() {
        // given
        List<Holiday> holidays = List.of(
                new Holiday(MonthDay.of(1, 1), ONLY_SUNDAY),
                new Holiday(MonthDay.of(3, 5), ALL),
                new Holiday(MonthDay.of(5, 8), ONLY_SUNDAY)
        );

        // when
        List<LocalDate> testSet = List.of(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 1, 2),
                LocalDate.of(2022, 3, 5),
                LocalDate.of(2022, 3, 6),
                LocalDate.of(2022, 3, 7),
                LocalDate.of(2022, 3, 8),
                LocalDate.of(2022, 5, 8),
                LocalDate.of(2022, 5, 9),
                LocalDate.of(2022, 5, 10)
        );

        // then
        test(holidays, testSet, true, true, true, true, true, false, true, true, false);
    }

    @Test
    @DisplayName("단순 공휴일 처리 확인")
    void isHoliday() {
        // given
        List<Holiday> holidays = List.of(
                new Holiday(MonthDay.of(1, 1), NONE),
                new Holiday(MonthDay.of(6, 13), NONE),
                new Holiday(MonthDay.of(12, 31), NONE)
        );

        // when
        List<LocalDate> testSet = List.of(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 6, 13),
                LocalDate.of(2022, 12, 31),
                LocalDate.of(2022, 12, 30),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 6, 13),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2023, 12, 30)
        );

        // then
        test(holidays, testSet, true, true, true, false, true, true, true, false);
    }

    @Test
    @DisplayName("2023 법정공휴일 처리 확인")
    void test2023Holidays() {
        // given
        List<Holiday> holidays = List.of(
                new Holiday(MonthDay.of(1, 1), NONE),
                new Holiday(MonthDay.of(3, 1), ALL),
                new Holiday(MonthDay.of(5, 5), ALL),
                new Holiday(MonthDay.of(6, 6), NONE),
                new Holiday(MonthDay.of(8, 15), ALL),
                new Holiday(MonthDay.of(10, 3), ALL),
                new Holiday(MonthDay.of(10, 9), ALL),
                new Holiday(MonthDay.of(12, 25), NONE),
                new Holiday(MonthDay.of(1, 21), ONLY_SUNDAY),
                new Holiday(MonthDay.of(1, 22), ALL),
                new Holiday(MonthDay.of(1, 23), ONLY_SUNDAY),
                new Holiday(MonthDay.of(5, 27), ALL),
                new Holiday(MonthDay.of(9, 28), ONLY_SUNDAY),
                new Holiday(MonthDay.of(9, 29), ALL),
                new Holiday(MonthDay.of(9, 30), ONLY_SUNDAY)
        );
        when(parser.parse(any())).thenReturn(new ArrayList<>(holidays));

        // when
        Set<LocalDate> actual = service.getHolidays(2023);

        // then
        assertThat(actual).containsExactlyInAnyOrder(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 21),
                LocalDate.of(2023, 1, 22),
                LocalDate.of(2023, 1, 23),
                LocalDate.of(2023, 1, 24),
                LocalDate.of(2023, 3, 1),
                LocalDate.of(2023, 5, 5),
                LocalDate.of(2023, 5, 27),
                LocalDate.of(2023, 5, 29),
                LocalDate.of(2023, 6, 6),
                LocalDate.of(2023, 8, 15),
                LocalDate.of(2023, 9, 28),
                LocalDate.of(2023, 9, 29),
                LocalDate.of(2023, 9, 30),
                LocalDate.of(2023, 10, 3),
                LocalDate.of(2023, 10, 9),
                LocalDate.of(2023, 12, 25)
        );
    }

    private void test(List<Holiday> holidays, List<LocalDate> testSet, Boolean... expected) {
        // given
        when(parser.parse(any())).thenReturn(new ArrayList<>(holidays));

        // when
        List<Boolean> result = new ArrayList<>(testSet.size());
        for (LocalDate date : testSet) {
            result.add(service.isHoliday(date));
        }

        // then
        assertThat(result).containsExactly(expected);
    }
}