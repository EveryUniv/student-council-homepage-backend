package com.dku.council.infra.bus.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShuttleBusProviderTest {

    private ShuttleBusProvider provider;


    @BeforeEach
    void defaultSetup() {
        provider = new ShuttleBusProvider();
    }

    @Test
    @DisplayName("버스 남은시간 잘 wrapping 하는지")
    void retrieveBusArrival() {
        // when
        List<BusArrival> result = provider.retrieveBusArrival(BusStation.DKU_GATE);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BusStatus.PREDICT);
        assertThat(result.get(0).getLocationNo1()).isEqualTo(1);
        assertThat(result.get(0).getPredictTimeSec1()).isEqualTo(1000);
    }
}