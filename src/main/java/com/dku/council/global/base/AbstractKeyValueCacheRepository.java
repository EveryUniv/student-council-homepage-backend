package com.dku.council.global.base;

import com.dku.council.global.model.CacheObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractKeyValueCacheRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration cacheDuration;
    private final String cacheRootKey;


    protected AbstractKeyValueCacheRepository(StringRedisTemplate redisTemplate,
                                              ObjectMapper objectMapper,
                                              String cacheRootKey) {
        this(redisTemplate, objectMapper, null, cacheRootKey);
    }


    public <T> CacheObject<T> set(String key, T data, Instant now) {
        Instant expiredAt;
        if (cacheDuration != null) {
            expiredAt = now.plus(cacheDuration);
        } else {
            expiredAt = Instant.MAX;
        }

        CacheObject<T> obj = new CacheObject<>(expiredAt, data);
        String value;
        try {
            value = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForHash().put(cacheRootKey, key, value);
        return obj;
    }

    public <T> Optional<CacheObject<T>> getCacheObject(String key, Class<T> clazz, Instant now) {
        JavaType type = objectMapper.getTypeFactory().constructType(clazz);
        return getCacheObject(key, type, now);
    }

    public <T> Optional<CacheObject<T>> getCacheObject(String key, JavaType type, Instant now) {
        Object value = redisTemplate.opsForHash().get(cacheRootKey, key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            type = objectMapper.getTypeFactory().constructParametricType(CacheObject.class, type);
            CacheObject<T> auth = objectMapper.readValue((String) value, type);
            if (now.isAfter(auth.getExpiresAt())) {
                remove(key);
                return Optional.empty();
            }
            return Optional.of(auth);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Optional<T> get(String key, Class<T> clazz, Instant now) {
        return getCacheObject(key, clazz, now)
                .map(CacheObject::getValue);
    }

    public <T> Optional<T> get(String key, JavaType type, Instant now) {
        Optional<CacheObject<T>> cacheObject = getCacheObject(key, type, now);
        return cacheObject.map(CacheObject::getValue);
    }

    public boolean remove(String key) {
        return redisTemplate.opsForHash().delete(cacheRootKey, key) > 0;
    }
}
