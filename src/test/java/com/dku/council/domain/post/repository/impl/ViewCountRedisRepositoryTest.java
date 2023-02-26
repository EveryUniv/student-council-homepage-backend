package com.dku.council.domain.post.repository.impl;

import com.dku.council.global.config.RedisKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ViewCountRedisRepositoryTest {

    @Autowired
    private ViewCountRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @BeforeEach
    void setup() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }

    @Test
    @DisplayName("조회수 카운팅 캐시에 잘 입력되는가?")
    void addPostLike() {
        // given
        String userIdentifier = "User";
        String key = repository.makeEntryKey(10L, userIdentifier);
        Instant now = Instant.now();

        // when
        repository.put(10L, userIdentifier, 100, now);

        // then
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_VIEW_COUNT_SET_KEY, key);
        long expectedEpoch = now.plus(100, ChronoUnit.MINUTES).getEpochSecond();
        assertThat(value).isEqualTo(String.valueOf(expectedEpoch));
    }

    @Test
    @DisplayName("유저가 이미 조회한 적 있는지? - 없는 경우")
    void isAlreadyContainsNoCached() {
        // given
        String userIdentifier = "User";
        Instant now = Instant.now();

        // when
        boolean result = repository.isAlreadyContains(10L, userIdentifier, now);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("유저가 이미 조회한 적 있는지? - 있는 경우")
    void isAlreadyContainsCached() {
        // given
        String userIdentifier = "User";
        Instant now = Instant.now();

        // when
        repository.put(10L, userIdentifier, 10, now);
        boolean result = repository.isAlreadyContains(10L, userIdentifier, now);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("유저가 이미 조회한 적 있는지? - 있지만 만료된 경우")
    void isAlreadyContainsExpired() {
        // given
        String userIdentifier = "User";
        Instant now = Instant.now();

        // when
        repository.put(10L, userIdentifier, 10, now);
        boolean result = repository.isAlreadyContains(10L, userIdentifier, now.plus(11, ChronoUnit.MINUTES));

        // then
        assertThat(result).isEqualTo(false);
    }
}