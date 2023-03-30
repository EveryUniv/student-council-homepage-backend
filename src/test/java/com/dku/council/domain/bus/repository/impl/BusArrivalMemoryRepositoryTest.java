package com.dku.council.domain.bus.repository.impl;

import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.mock.BusArrivalMock;
import com.dku.council.util.FullIntegrationTest;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class BusArrivalMemoryRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private BusArrivalRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bus.cache-time}")
    private Duration busCacheTime;


    @Test
    @DisplayName("도착 정보를 잘 가져올 수 있는가")
    void getArrivals() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        put(stationId, arrivals, now);

        // when
        CachedBusArrivals cached = repository.getArrivals(stationId, now).orElseThrow();

        // then
        assertThat(cached.getCapturedAt().getEpochSecond()).isEqualTo(now.getEpochSecond());
        assertThat(cached.getArrivals()).containsExactlyInAnyOrderElementsOf(arrivals);
    }

    @Test
    @DisplayName("도착 정보가 없으면 null반환")
    void getArrivalsWithNoData() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();

        // when
        Optional<CachedBusArrivals> cached = repository.getArrivals(stationId, now);

        // then
        assertThat(cached).isEmpty();
    }

    @Test
    @DisplayName("도착 정보가 만료되었으면 null반환")
    void getArrivalsWithExpiredData() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        put(stationId, arrivals, now.minusSeconds(busCacheTime.getSeconds() + 1));

        // when
        Optional<CachedBusArrivals> cached = repository.getArrivals(stationId, now);

        // then
        assertThat(cached).isEmpty();
    }

    @Test
    @DisplayName("도착 정보가 잘 캐싱되는가")
    void cacheArrivals() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        List<BusArrival> arrivals = BusArrivalMock.createList(5);

        // when
        repository.cacheArrivals(stationId, arrivals, now);

        // then
        CachedBusArrivals actualCached = get(stationId);
        assertThat(actualCached.getCapturedAt().getEpochSecond()).isEqualTo(now.getEpochSecond());
        assertThat(actualCached.getArrivals()).containsExactlyInAnyOrderElementsOf(arrivals);
    }

    public CachedBusArrivals get(String stationId) {
        try {
            Object value = redisTemplate.opsForHash().get(RedisKeys.BUS_ARRIVAL_KEY, stationId);
            TypeFactory typeFactory = objectMapper.getTypeFactory();

            JavaType collectionType = typeFactory.constructCollectionType(List.class, BusArrival.class);
            JavaType type = typeFactory.constructParametricType(CacheObject.class, collectionType);

            CacheObject<List<BusArrival>> cacheObject = objectMapper.readValue((String) value, type);
            return new CachedBusArrivals(cacheObject, busCacheTime);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String stationId, List<BusArrival> cache, Instant now) {
        try {
            CacheObject<List<BusArrival>> obj = new CacheObject<>(now.plus(busCacheTime), cache);
            String value = objectMapper.writeValueAsString(obj);
            redisTemplate.opsForHash().put(RedisKeys.BUS_ARRIVAL_KEY, stationId, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}