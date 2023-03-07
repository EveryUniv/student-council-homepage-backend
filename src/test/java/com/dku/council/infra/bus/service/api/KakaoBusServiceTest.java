package com.dku.council.infra.bus.service.api;

import com.dku.council.infra.bus.exception.CannotGetBusArrivalException;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import com.dku.council.mock.ServerMock;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KakaoBusServiceTest {

    private static MockWebServer mockServer;

    private KakaoBusService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new KakaoBusService(webClient, apiPath);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("버스 정보 잘 가져오는지")
    public void retrieveBusArrival() {
        // given
        ServerMock.json(mockServer, "/bus/kakao");

        // when
        List<BusArrival> arrivals = service.retrieveBusArrival("111111");

        // then
        assertThat(arrivals.size()).isEqualTo(2);
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
        assertThat(arrival.getLocationNo1()).isEqualTo(5);
        assertThat(arrival.getLocationNo2()).isEqualTo(9);
    }

    @Test
    @DisplayName("실패 - 정류장이 없는 경우")
    public void failedRetrieveBusArrivalByNoResult() {
        // given
        ServerMock.json(mockServer, "/bus/kakao-failed");

        // when & then
        Assertions.assertThrows(CannotGetBusArrivalException.class, () ->
                service.retrieveBusArrival("111111"));
    }
}