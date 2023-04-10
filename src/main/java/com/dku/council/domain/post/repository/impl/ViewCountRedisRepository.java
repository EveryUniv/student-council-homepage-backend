package com.dku.council.domain.post.repository.impl;

import com.dku.council.domain.post.repository.ViewCountMemoryRepository;
import com.dku.council.global.base.AbstractKeyValueCacheRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;

@Repository
public class ViewCountRedisRepository extends AbstractKeyValueCacheRepository implements ViewCountMemoryRepository {

    public ViewCountRedisRepository(StringRedisTemplate redisTemplate,
                                    ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, RedisKeys.POST_VIEW_COUNT_SET_KEY);
    }

    @Override
    public boolean isAlreadyContains(Long postId, String userIdentifier, Instant now) {
        String key = RedisKeys.combine(postId, userIdentifier);
        return get(key, String.class, now).isPresent();
    }

    @Override
    public void put(Long postId, String userIdentifier, Duration expiresAfter, Instant now) {
        String key = RedisKeys.combine(postId, userIdentifier);
        set(key, "", now, expiresAfter);
    }
}
