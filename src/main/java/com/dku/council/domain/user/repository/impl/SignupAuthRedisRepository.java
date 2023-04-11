package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.global.base.AbstractKeyValueCacheRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Repository
public class SignupAuthRedisRepository extends AbstractKeyValueCacheRepository implements SignupAuthRepository {

    private final Duration cacheDuration;

    protected SignupAuthRedisRepository(StringRedisTemplate redisTemplate,
                                        ObjectMapper objectMapper,
                                        @Value("${app.auth.signup-expires}") Duration cacheDuration) {
        super(redisTemplate, objectMapper, RedisKeys.SIGNUP_AUTH_KEY);
        this.cacheDuration = cacheDuration;
    }

    public void setAuthPayload(String signupToken, String authName, Object data, Instant now) {
        String key = makeEntryKey(signupToken, authName);
        set(key, data, now, cacheDuration);
    }

    public <T> Optional<T> getAuthPayload(String signupToken, String authName, Class<T> clazz, Instant now) {
        String key = makeEntryKey(signupToken, authName);
        return get(key, clazz, now);
    }

    public boolean deleteAuthPayload(String signupToken, String authName) {
        String key = makeEntryKey(signupToken, authName);
        return remove(key);
    }

    public String makeEntryKey(String signupToken, String authName) {
        return signupToken + RedisKeys.KEY_DELIMITER + authName;
    }
}
