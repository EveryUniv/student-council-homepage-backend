package com.dku.council.infra.bus.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShuttleBusProviderTest {

    private ShuttleBusProvider provider;


    @BeforeEach
    void setup() {
        provider = new ShuttleBusProvider();
    }

    @Test
    @DisplayName("빈 리스트 반환")
    void retrieveBusArrival() {
        // when
        List<BusArrival> result = provider.retrieveBusArrival(BusStation.DKU_GATE);
        List<BusArrival> result2 = provider.retrieveBusArrival(BusStation.BEAR_STATUE);

        // then
        assertThat(result).isEmpty();
        assertThat(result2).isEmpty();
    }
}