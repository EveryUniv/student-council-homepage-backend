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
import static org.assertj.core.api.Assertions.fail;

class GGBusServiceTest {

    private static MockWebServer mockServer;

    private GGBusService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new GGBusService(webClient, apiPath, "serviceKey");
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("버스 정보 잘 가져오는지")
    public void retrieveBusArrival() {
        // given
        ServerMock.xml(mockServer, "/bus/getBusArrivalList");

        // when
        List<BusArrival> arrivals = service.retrieveBusArrival("111111");

        // then
        assertThat(arrivals.size()).isEqualTo(2);
        assertThat(arrivals.get(0).getStatus()).isEqualTo(BusStatus.RUN);
        assertThat(arrivals.get(0).getBusNo()).isEqualTo("102");
        assertThat(arrivals.get(0).getLocationNo1()).isEqualTo(1);
        assertThat(arrivals.get(0).getPredictTimeSec1()).isEqualTo(1);
        assertThat(arrivals.get(0).getPlateNo1()).isEqualTo("경기70아6909");
        assertThat(arrivals.get(0).getLocationNo2()).isEqualTo(0);
        assertThat(arrivals.get(0).getPredictTimeSec2()).isEqualTo(0);
        assertThat(arrivals.get(0).getPlateNo2()).isEmpty();
        assertThat(arrivals.get(1).getStatus()).isEqualTo(BusStatus.WAITING);
        assertThat(arrivals.get(1).getBusNo()).isEqualTo("720-3");
        assertThat(arrivals.get(1).getLocationNo1()).isEqualTo(2);
        assertThat(arrivals.get(1).getPredictTimeSec1()).isEqualTo(6);
        assertThat(arrivals.get(1).getPlateNo1()).isEqualTo("경기70아1212");
        assertThat(arrivals.get(1).getLocationNo2()).isEqualTo(5);
        assertThat(arrivals.get(1).getPredictTimeSec2()).isEqualTo(16);
        assertThat(arrivals.get(1).getPlateNo2()).isEqualTo("경기70아1512");
    }

    @Test
    @DisplayName("실패 - 결과 없는 경우")
    public void failedRetrieveBusArrivalByNoResult() {
        // given
        ServerMock.xml(mockServer, "/bus/getBusArrivalList-failed");

        try {
            // when
            service.retrieveBusArrival("111111");
            fail("CannotGetBusArrivalException is not thrown.");
        } catch (CannotGetBusArrivalException e) {
            // then
            assertThat(e.getCause().getMessage()).isEqualTo("결과가 존재하지 않습니다.");
        }
    }
}