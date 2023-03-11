package com.dku.council.infra.bus.predict.impl;

import com.dku.council.domain.bus.model.BusStation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusTimeTablePredictServiceTest {

    @Mock
    private TimeTableParser parser;

    @Mock
    private TimeTable table;

    @InjectMocks
    private BusTimeTablePredictService service;

    @Test
    @DisplayName("다음 버스 도착 예측")
    void remainingNextBusArrival() {
        // given
        LocalTime now = LocalTime.of(11, 0);

        when(table.getFirstTime()).thenReturn(LocalTime.of(10, 0));
        when(table.getLastTime()).thenReturn(LocalTime.of(20, 0));
        when(table.remainingNextBusArrival(now)).thenReturn(Duration.ZERO);
        when(parser.parse("/bustable/dkugate/11.table")).thenReturn(table);

        // when
        Duration time = service.remainingNextBusArrival("11", BusStation.DKU_GATE, now);

        // then
        assertThat(time).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("운행시간이 아닌 경우 - 막차 12시 이전")
    void whenNotRunning() {
        // given
        LocalTime now = LocalTime.of(8, 0);
        LocalTime now2 = LocalTime.of(21, 0);

        when(table.getFirstTime()).thenReturn(LocalTime.of(10, 0));
        when(table.getLastTime()).thenReturn(LocalTime.of(20, 0));
        when(parser.parse("/bustable/dkugate/11.table")).thenReturn(table);

        // when
        Duration time = service.remainingNextBusArrival("11", BusStation.DKU_GATE, now);
        Duration time2 = service.remainingNextBusArrival("11", BusStation.DKU_GATE, now2);

        // then
        assertThat(time).isNull();
        assertThat(time2).isNull();
    }

    @Test
    @DisplayName("운행시간이 아닌 경우 - 막차 12시 이후")
    void whenNotRunningLastTimeAfter12() {
        // given
        LocalTime now = LocalTime.of(8, 0);
        LocalTime now2 = LocalTime.of(20, 0);

        when(table.getFirstTime()).thenReturn(LocalTime.of(10, 0));
        when(table.getLastTime()).thenReturn(LocalTime.of(1, 0));
        when(table.remainingNextBusArrival(now2)).thenReturn(Duration.ZERO);
        when(parser.parse("/bustable/dkugate/11.table")).thenReturn(table);

        // when
        Duration time = service.remainingNextBusArrival("11", BusStation.DKU_GATE, now);
        Duration time2 = service.remainingNextBusArrival("11", BusStation.DKU_GATE, now2);

        // then
        assertThat(time).isNull();
        assertThat(time2).isEqualTo(Duration.ZERO);
    }
}