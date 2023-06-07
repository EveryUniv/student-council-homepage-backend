package com.dku.council.domain.bus.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayParser parser;

    @InjectMocks
    private HolidayService service;


    @Test
    @DisplayName("휴일인지 확인한다.")
    void isHoliday() {
        // given
        Set<MonthDay> holidays = Set.of(
                MonthDay.of(1, 1),
                MonthDay.of(2, 3),
                MonthDay.of(12, 31)
        );
        when(parser.parse()).thenReturn(holidays);

        // when
        List<LocalDate> testSet = List.of(
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 2, 3),
                LocalDate.of(2021, 12, 31),
                LocalDate.of(2022, 1, 4),
                LocalDate.of(2022, 2, 3),
                LocalDate.of(2022, 12, 30)
        );
        List<Boolean> result = new ArrayList<>(testSet.size());
        for (LocalDate date : testSet) {
            result.add(service.isHoliday(date));
        }

        // then
        assertThat(result).containsExactly(true, true, true, false, true, false);
    }
}