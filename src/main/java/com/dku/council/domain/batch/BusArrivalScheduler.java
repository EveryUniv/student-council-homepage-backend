package com.dku.council.domain.batch;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusArrivalScheduler {

    private final BusService busService;

    @Scheduled(fixedDelayString = "${bus.cache-time}")
    public void schedule() {
        for (BusStation station : BusStation.values()) {
            busService.cacheBusArrival(station);
        }
    }
}
