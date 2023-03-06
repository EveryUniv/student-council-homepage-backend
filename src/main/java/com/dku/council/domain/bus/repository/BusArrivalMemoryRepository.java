package com.dku.council.domain.bus.repository;

import com.dku.council.domain.bus.model.BusArrival;
import com.dku.council.domain.bus.model.repository.CachedBusArrivals;

import java.time.Instant;
import java.util.List;

public interface BusArrivalMemoryRepository {
    CachedBusArrivals getArrivals(String stationId, Instant now);

    CachedBusArrivals cacheArrivals(String stationId, List<BusArrival> arrivals, Instant now);
}
