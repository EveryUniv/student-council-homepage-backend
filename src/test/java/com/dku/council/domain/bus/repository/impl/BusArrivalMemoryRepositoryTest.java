package com.dku.council.domain.bus.repository.impl;

import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.mock.BusArrivalMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

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

    @Test
    @DisplayName("도착 정보 중복 캐싱시 덮어쓰기")
    void cacheArrivalsDuplicated() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        Instant now2 = Instant.now().plusSeconds(600);
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        List<BusArrival> arrivals2 = BusArrivalMock.createList(2);

        // when
        repository.cacheArrivals(stationId, arrivals, now);
        repository.cacheArrivals(stationId, arrivals2, now2);

        // then
        CachedBusArrivals actualCached = get(stationId);
        assertThat(actualCached.getCapturedAt().getEpochSecond()).isEqualTo(now2.getEpochSecond());
        assertThat(actualCached.getArrivals()).containsExactlyInAnyOrderElementsOf(arrivals2);
    }

    public CachedBusArrivals get(String stationId) {
        try {
            Object value = redisTemplate.opsForHash().get(RedisKeys.BUS_ARRIVAL_KEY, stationId);

            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType type = typeFactory.constructParametricType(CacheObject.class, CachedBusArrivals.class);

            CacheObject<CachedBusArrivals> obj = objectMapper.readValue((String) value, type);
            return obj.getValue();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String stationId, List<BusArrival> cache, Instant now) {
        try {
            CachedBusArrivals arrivals = new CachedBusArrivals(now, cache);
            CacheObject<CachedBusArrivals> obj = new CacheObject<>(Instant.MAX, arrivals);
            String value = objectMapper.writeValueAsString(obj);
            redisTemplate.opsForHash().put(RedisKeys.BUS_ARRIVAL_KEY, stationId, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}