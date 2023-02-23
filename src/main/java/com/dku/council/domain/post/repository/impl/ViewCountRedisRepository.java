package com.dku.council.domain.post.repository.impl;

import com.dku.council.domain.post.repository.ViewCountMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
@RequiredArgsConstructor
public class ViewCountRedisRepository implements ViewCountMemoryRepository {

    // TODO Redis Key들은 한 곳에 모아두기
    public static final String POST_VIEW_COUNT_SET_KEY = "PostViewSet";
    public static final String KEY_DELIMITER = ":";

    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public boolean isAlreadyContains(Long postId, String userIdentifier, Instant now) {
        String key = makeEntryKey(postId, userIdentifier);
        Object value = redisTemplate.opsForHash().get(POST_VIEW_COUNT_SET_KEY, key);
        if (value == null) {
            return false;
        }

        long expiresAt = (long) value;
        if (now.isAfter(Instant.ofEpochSecond(expiresAt))) {
            redisTemplate.opsForHash().delete(POST_VIEW_COUNT_SET_KEY, key);
            return false;
        }

        return true;
    }

    @Override
    public void put(Long postId, String userIdentifier, long expiresAfter, Instant now) {
        String key = makeEntryKey(postId, userIdentifier);
        long expiresAt = now.plus(expiresAfter, ChronoUnit.MINUTES).getEpochSecond();
        redisTemplate.opsForHash().put(POST_VIEW_COUNT_SET_KEY, key, expiresAt);
    }

    public String makeEntryKey(Long postId, String userIdentifier) {
        return postId.toString() + KEY_DELIMITER + userIdentifier;
    }
}
