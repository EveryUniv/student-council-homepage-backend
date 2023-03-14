package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.model.dto.ResponseBusArrivalDto;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.OpenApiBusService;
import com.dku.council.mock.BusArrivalMock;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    private final Clock clock = ClockUtil.create();
    private BusService service;

    @Mock
    private OpenApiBusService openApiBusService;

    @Mock
    private BusArrivalRepository memoryRepository;

    @BeforeEach
    public void setup() {
        this.service = new BusService(clock, openApiBusService, memoryRepository);
    }

    @Test
    @DisplayName("버스 도착 목록이 잘 반환되는지 - 최근에 조회한 적 없는 경우")
    void listBusArrivalNoCached() {
        // given
        BusStation station = BusStation.DKU_GATE;
        Instant now = Instant.now(clock);
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        CachedBusArrivals cached = new CachedBusArrivals(now, arrivals);
        when(memoryRepository.cacheArrivals(eq(station.name()), any(), any())).thenReturn(cached);

        // when
        ResponseBusArrivalDto dto = service.listBusArrival(BusStation.DKU_GATE);

        // then
        verify(openApiBusService).retrieveBusArrival(station);
        assertThat(dto.getCapturedAt().getEpochSecond()).isEqualTo(now.getEpochSecond());
        assertThat(dto.getBusArrivalList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("버스 도착 목록이 잘 반환되는지 - 최근에 조회한 적 있는 경우")
    void listBusArrivalCached() {
        // given
        BusStation station = BusStation.DKU_GATE;
        Instant now = Instant.now(clock);
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        CachedBusArrivals cached = new CachedBusArrivals(now, arrivals);
        when(memoryRepository.getArrivals(station.name(), now)).thenReturn(Optional.of(cached));

        // when
        ResponseBusArrivalDto dto = service.listBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(dto.getCapturedAt().getEpochSecond()).isEqualTo(now.getEpochSecond());
        assertThat(dto.getBusArrivalList().size()).isEqualTo(5);
    }
}