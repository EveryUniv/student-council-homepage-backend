package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.provider.BusArrivalProvider;
import com.dku.council.infra.bus.service.provider.GGBusProvider;
import com.dku.council.infra.bus.service.provider.TownBusProvider;
import com.dku.council.mock.BusArrivalMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OpenApiBusServiceTest {
    @Mock
    private GGBusProvider ggBusProvider;

    @Mock
    private TownBusProvider townBusProvider;

    private OpenApiBusService service;


    @BeforeEach
    public void setup() {
        List<BusArrivalProvider> providers = List.of(ggBusProvider, townBusProvider);
        service = new OpenApiBusService(providers);
    }

    @Test
    @DisplayName("여러 API를 합쳐 잘 필터링 하는지")
    public void retrieveBusArrival() {
        // given
        BusStation station = BusStation.DKU_GATE;
        List<BusArrival> list1 = List.of(
                BusArrivalMock.create("24"),
                BusArrivalMock.create("101"),
                BusArrivalMock.create("1234"),
                BusArrivalMock.create("7007-1"),
                BusArrivalMock.create("102")
        );
        Mockito.when(ggBusProvider.retrieveBusArrival(station)).thenReturn(list1);
        Mockito.when(ggBusProvider.getProviderPrefix()).thenReturn("GG_");

        List<BusArrival> list2 = List.of(
                BusArrivalMock.create("24"),
                BusArrivalMock.create("1234"),
                BusArrivalMock.create("7007-1"),
                BusArrivalMock.create("102")
        );
        Mockito.when(townBusProvider.retrieveBusArrival(station)).thenReturn(list2);
        Mockito.when(townBusProvider.getProviderPrefix()).thenReturn("T_");

        // when
        List<BusArrival> arrivals = service.retrieveBusArrival(station);

        // then
        List<String> busNumbers = arrivals.stream()
                .map(BusArrival::getBusNo)
                .collect(Collectors.toList());
        assertThat(arrivals.size()).isEqualTo(3);
        assertThat(busNumbers).containsExactlyInAnyOrderElementsOf(
                List.of("24", "102", "7007-1")
        );
    }
}