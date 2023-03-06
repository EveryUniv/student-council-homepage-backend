package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.api.GGBusService;
import com.dku.council.infra.bus.service.api.KakaoBusService;
import com.dku.council.mock.BusArrivalMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OpenApiBusServiceTest {
    @Mock
    private GGBusService ggBusService;

    @Mock
    private KakaoBusService kakaoBusService;

    @InjectMocks
    private OpenApiBusService service;


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
        Mockito.when(ggBusService.retrieveBusArrival(station.getGgNodeId()))
                .thenReturn(list1);
        Mockito.when(ggBusService.getBusId(any()))
                .thenAnswer(invo -> "GG" + invo.getArgument(0));

        List<BusArrival> list2 = List.of(
                BusArrivalMock.create("24"),
                BusArrivalMock.create("1234"),
                BusArrivalMock.create("7007-1"),
                BusArrivalMock.create("102")
        );
        Mockito.when(kakaoBusService.retrieveBusArrival(station.getKakaoNodeId()))
                .thenReturn(list2);
        Mockito.when(kakaoBusService.getBusId(any()))
                .thenAnswer(invo -> "K" + invo.getArgument(0));

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