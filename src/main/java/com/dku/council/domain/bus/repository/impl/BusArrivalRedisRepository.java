package com.dku.council.domain.bus.repository.impl;

import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.global.base.AbstractKeyValueCacheRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.infra.bus.model.BusArrival;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class BusArrivalRedisRepository extends AbstractKeyValueCacheRepository implements BusArrivalRepository {

    private final ObjectMapper objectMapper;
    private final Duration busCacheTime;

    protected BusArrivalRedisRepository(StringRedisTemplate redisTemplate,
                                        ObjectMapper objectMapper,
                                        @Value("${bus.cache-time}") Duration busCacheTime) {
        super(redisTemplate, objectMapper, busCacheTime, RedisKeys.BUS_ARRIVAL_KEY);
        this.objectMapper = objectMapper;
        this.busCacheTime = busCacheTime;
    }

    public Optional<CachedBusArrivals> getArrivals(String stationId, Instant now) {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, BusArrival.class);
        Optional<CacheObject<List<BusArrival>>> cacheObject = getCacheObject(stationId, type, now);
        return cacheObject.map(e -> new CachedBusArrivals(e, busCacheTime));
    }

    public CachedBusArrivals cacheArrivals(String stationId, List<BusArrival> arrivals, Instant now) {
        return new CachedBusArrivals(set(stationId, arrivals, now), busCacheTime);
    }
}
