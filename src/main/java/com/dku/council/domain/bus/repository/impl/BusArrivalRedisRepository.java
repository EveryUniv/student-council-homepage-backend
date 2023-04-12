package com.dku.council.domain.bus.repository.impl;

import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.global.base.AbstractKeyValueCacheRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.infra.bus.model.BusArrival;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class BusArrivalRedisRepository extends AbstractKeyValueCacheRepository implements BusArrivalRepository {

    protected BusArrivalRedisRepository(StringRedisTemplate redisTemplate,
                                        ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, RedisKeys.BUS_ARRIVAL_KEY);
    }

    public Optional<CachedBusArrivals> getArrivals(String stationId, Instant now) {
        Optional<CacheObject<CachedBusArrivals>> cacheObject = getCacheObject(stationId, CachedBusArrivals.class, now);
        return cacheObject.map(CacheObject::getValue);
    }

    public CachedBusArrivals cacheArrivals(String stationId, List<BusArrival> arrivals, Instant now) {
        CachedBusArrivals cachePayload = new CachedBusArrivals(now, arrivals);
        set(stationId, cachePayload, now);
        return cachePayload;
    }
}
