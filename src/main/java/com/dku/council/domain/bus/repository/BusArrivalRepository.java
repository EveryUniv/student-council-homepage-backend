package com.dku.council.domain.bus.repository;

import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.infra.bus.model.BusArrival;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BusArrivalRepository {

    /**
     * 버스 도착 정보를 가져온다.
     *
     * @param stationId 정류장 id
     * @param now       현재 시간
     * @return 버스 도착 정보
     */
    Optional<CachedBusArrivals> getArrivals(String stationId, Instant now);

    /**
     * 버스 도착 정보를 캐시한다.
     *
     * @param stationId 정류장 id
     * @param arrivals  버스 도착 정보
     * @param now       현재 시간
     * @return 캐시된 버스 도착 정보
     */
    CachedBusArrivals cacheArrivals(String stationId, List<BusArrival> arrivals, Instant now);
}
