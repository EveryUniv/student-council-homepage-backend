package com.dku.council.infra.bus.service.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.exception.InvalidBusStationException;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShuttleBusProviderTest {

    @Mock
    private ShuttleTimeTable table;

    private ShuttleBusProvider provider;


    @BeforeEach
    void defaultSetup() {
        setup(ClockUtil.create(LocalTime.of(13, 10)));
    }

    void setup(Clock clock) {
        provider = new ShuttleBusProvider(clock, table);
    }


    @Test
    @DisplayName("지원하지 않는 버스 정류소인 경우")
    void failedRetrieveBusArrivalByInvalidBusStation() {
        // when & then
        Assertions.assertThrows(InvalidBusStationException.class, () ->
                provider.retrieveBusArrival(BusStation.BEAR_STATUE));
    }

    @Test
    @DisplayName("버스 없는 시간인 경우1")
    void retrieveBusArrivalWithEmptyBus1() {
        // given
        setup(ClockUtil.create(LocalTime.of(22, 10)));

        // when
        List<BusArrival> result = provider.retrieveBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("버스 없는 시간인 경우2")
    void retrieveBusArrivalWithEmptyBus2() {
        // given
        setup(ClockUtil.create(LocalTime.of(5, 10)));

        // when
        List<BusArrival> result = provider.retrieveBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("버스 남은시간 잘 wrapping 하는지")
    void retrieveBusArrival() {
        // given
        when(table.remainingNextBusArrival(any())).thenReturn(Duration.ofSeconds(1000));

        // when
        List<BusArrival> result = provider.retrieveBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BusStatus.RUN);
        assertThat(result.get(0).getLocationNo1()).isEqualTo(1);
        assertThat(result.get(0).getPredictTimeSec1()).isEqualTo(1000);
    }
}