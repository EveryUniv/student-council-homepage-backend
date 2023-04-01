package com.dku.council.infra.bus.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.exception.CannotGetBusArrivalException;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TownBusProviderTest extends AbstractMockServerTest {

    private TownBusProvider service;

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new TownBusProvider(webClient, apiPath);
    }

    @Test
    @DisplayName("버스 정보 잘 가져오는지")
    public void retrieveBusArrival() {
        // given
        mockJson("/bus/kakao");

        // when
        List<BusArrival> arrivals = service.retrieveBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(arrivals.size()).isEqualTo(3);
        BusArrival arrival = arrivals.stream()
                .filter(ent -> ent.getBusNo().equals("24"))
                .findFirst()
                .orElseThrow();

        assertThat(arrival.getStatus()).isEqualTo(BusStatus.RUN);
        assertThat(arrival.getBusNo()).isEqualTo("24");
        assertThat(arrival.getPlateNo1()).isEqualTo("경기78아9092");
        assertThat(arrival.getPlateNo2()).isEqualTo("경기78아8202");
        assertThat(arrival.getPredictTimeSec1()).isEqualTo(343);
        assertThat(arrival.getPredictTimeSec2()).isEqualTo(904);
        assertThat(arrival.getLocationNo1()).isEqualTo(2);
        assertThat(arrival.getLocationNo2()).isEqualTo(9);
    }

    @Test
    @DisplayName("실패 - 정류장이 없는 경우")
    public void failedRetrieveBusArrivalByNoResult() {
        // given
        mockJson("/bus/kakao-failed");

        // when & then
        Assertions.assertThrows(CannotGetBusArrivalException.class, () ->
                service.retrieveBusArrival(BusStation.DKU_GATE));
    }
}