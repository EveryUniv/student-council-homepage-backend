package com.dku.council.domain.bus.model;

import com.dku.council.infra.bus.model.BusArrival;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CachedBusArrivals {
    private final Instant capturedAt;
    private final List<BusArrival> arrivals;
}
