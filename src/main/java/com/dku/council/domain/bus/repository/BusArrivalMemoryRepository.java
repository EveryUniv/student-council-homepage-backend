package com.dku.council.domain.bus.repository;

import com.dku.council.domain.bus.model.repository.CachedBusArrivals;
import com.dku.council.infra.bus.model.BusArrival;

import java.time.Instant;
import java.util.List;

public interface BusArrivalMemoryRepository {

    /**
     * 캐싱된 버스 도착 정보를 가져옵니다.
     *
     * @param stationId 정류소 ID
     * @param now       현재 시각
     * @return 버스 도착 정보 목록. 없으면 null반환
     */
    CachedBusArrivals getArrivals(String stationId, Instant now);

    /**
     * 버스 도착 정보를 캐싱합니다.
     *
     * @param stationId 정류소 ID
     * @param arrivals  버스 도착 정보 목록
     * @param now       현재 시각
     * @return 캐싱된 버스 도착 정보 목록
     */
    CachedBusArrivals cacheArrivals(String stationId, List<BusArrival> arrivals, Instant now);
}
