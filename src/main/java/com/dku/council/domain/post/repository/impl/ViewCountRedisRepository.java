package com.dku.council.domain.post.repository.impl;

import com.dku.council.domain.post.repository.ViewCountMemoryRepository;
import com.dku.council.global.config.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
@RequiredArgsConstructor
public class ViewCountRedisRepository implements ViewCountMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isAlreadyContains(Long postId, String userIdentifier, Instant now) {
        String key = makeEntryKey(postId, userIdentifier);
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_VIEW_COUNT_SET_KEY, key);
        if (value == null) {
            return false;
        }

        long expiresAt = Long.parseLong((String) value);
        if (now.isAfter(Instant.ofEpochSecond(expiresAt))) {
            redisTemplate.opsForHash().delete(RedisKeys.POST_VIEW_COUNT_SET_KEY, key);
            return false;
        }

        return true;
    }

    @Override
    public void put(Long postId, String userIdentifier, long expiresAfter, Instant now) {
        String key = makeEntryKey(postId, userIdentifier);
        long expiresAt = now.plus(expiresAfter, ChronoUnit.MINUTES).getEpochSecond();
        redisTemplate.opsForHash().put(RedisKeys.POST_VIEW_COUNT_SET_KEY, key, String.valueOf(expiresAt));
    }

    public String makeEntryKey(Long postId, String userIdentifier) {
        return postId.toString() + RedisKeys.KEY_DELIMITER + userIdentifier;
    }
}
