package com.dku.council.infra.bus.service.provider;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShuttleBusProvider implements BusArrivalProvider {

    private final Clock clock;
    private final ShuttleTimeTable shuttleTimeTable;

    @Override
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        if (station != BusStation.DKU_GATE) {
            return List.of();
        }

        LocalTime now = LocalTime.now(clock);
        if (isOutboundTime(now)) {
            return List.of();
        }

        Duration remaining = shuttleTimeTable.remainingNextBusArrival(now);
        int remainingSec = (int) remaining.getSeconds();
        BusArrival arrival = new BusArrival(BusStatus.RUN,
                1, remainingSec, "",
                null, null, null,
                "shuttle"
        );

        return List.of(arrival);
    }

    @Override
    public String getProviderPrefix() {
        return "DKU_";
    }

    private boolean isOutboundTime(LocalTime time) {
        LocalTime lowerBound = LocalTime.of(6, 0);
        LocalTime upperBound = LocalTime.of(21, 0);
        return time.isAfter(upperBound) || time.isBefore(lowerBound);
    }
}
