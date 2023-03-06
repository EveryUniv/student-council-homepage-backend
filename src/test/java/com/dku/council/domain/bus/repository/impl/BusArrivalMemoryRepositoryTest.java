package com.dku.council.domain.bus.repository.impl;

import com.dku.council.common.AbstractContainerRedisTest;
import com.dku.council.common.OnlyDevTest;
import com.dku.council.domain.bus.model.repository.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.mock.BusArrivalMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@OnlyDevTest
class BusArrivalMemoryRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private BusArrivalMemoryRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bus.cache-time}")
    private Long busCacheTime;


    @Test
    @DisplayName("도착 정보를 잘 가져올 수 있는가")
    void getArrivals() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        put(stationId, arrivals, now);

        // when
        CachedBusArrivals cached = repository.getArrivals(stationId, now);

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
        CachedBusArrivals cached = repository.getArrivals(stationId, now);

        // then
        assertThat(cached).isNull();
    }

    @Test
    @DisplayName("도착 정보가 만료되었으면 null반환")
    void getArrivalsWithExpiredData() {
        // given
        String stationId = "stationId";
        Instant now = Instant.now();
        List<BusArrival> arrivals = BusArrivalMock.createList(5);
        put(stationId, arrivals, now.minusSeconds(busCacheTime + 1));

        // when
        CachedBusArrivals cached = repository.getArrivals(stationId, now);

        // then
        assertThat(cached).isNull();
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
            return objectMapper.readValue((String) value, CachedBusArrivals.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String stationId, List<BusArrival> cache, Instant now) {
        try {
            String value = objectMapper.writeValueAsString(new CachedBusArrivals(now, cache));
            redisTemplate.opsForHash().put(RedisKeys.BUS_ARRIVAL_KEY, stationId, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}