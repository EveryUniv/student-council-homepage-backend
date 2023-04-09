package com.dku.council.domain.post.repository.impl;

import com.dku.council.domain.post.repository.PostTimeMemoryRepository;
import com.dku.council.global.base.AbstractKeyValueCacheRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;

@Repository
public class PostTimeRedisRepository extends AbstractKeyValueCacheRepository implements PostTimeMemoryRepository {

    public PostTimeRedisRepository(StringRedisTemplate redisTemplate,
                                   ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, RedisKeys.POST_WRITE_KEY);
    }

    @Override
    public boolean isAlreadyContains(Long postId, String userIdentifier, Instant now) {
        String key = RedisKeys.combine(RedisKeys.POST_WRITE_KEY, postId, userIdentifier);
        return get(key, String.class, now).isPresent();
    }

    @Override
    public void put(Long postId, String userIdentifier, Duration expiresAfter, Instant now) {
        String key = RedisKeys.combine(RedisKeys.POST_WRITE_KEY, postId, userIdentifier);
        set(key, "", now, expiresAfter);
    }
}
