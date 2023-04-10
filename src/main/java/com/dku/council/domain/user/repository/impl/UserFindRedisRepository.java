package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.model.SMSAuth;
import com.dku.council.domain.user.repository.UserFindRepository;
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
public class UserFindRedisRepository extends AbstractKeyValueCacheRepository implements UserFindRepository {

    private final Duration cacheDuration;

    protected UserFindRedisRepository(StringRedisTemplate redisTemplate,
                                      ObjectMapper objectMapper,
                                      @Value("${app.auth.find-expires}") Duration cacheDuration) {
        super(redisTemplate, objectMapper, RedisKeys.USER_FIND_AUTH_KEY);
        this.cacheDuration = cacheDuration;
    }

    @Override
    public void setAuthCode(String token, String code, String phone, Instant now) {
        SMSAuth data = new SMSAuth(phone, code);
        set(token, data, now, cacheDuration);
    }

    @Override
    public Optional<SMSAuth> getAuthCode(String token, Instant now) {
        return get(token, SMSAuth.class, now);
    }

    @Override
    public boolean remove(String token) {
        return super.remove(token);
    }

    @Override
    public void deleteAuthCode(String token) {
        remove(token);
    }
}
