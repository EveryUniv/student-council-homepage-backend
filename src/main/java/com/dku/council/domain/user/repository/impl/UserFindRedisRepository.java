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

@Repository
public class UserFindRedisRepository extends AbstractKeyValueCacheRepository implements UserFindRepository {

    protected UserFindRedisRepository(StringRedisTemplate redisTemplate,
                                      ObjectMapper objectMapper,
                                      @Value("${app.auth.find-expires}") Duration cacheDuration) {
        super(redisTemplate, objectMapper, cacheDuration, RedisKeys.USER_FIND_AUTH_KEY);
    }

    @Override
    public void setPwdAuthCode(String token, String code, String phone, Instant now) {

    }

    @Override
    public SMSAuth getPwdAuthCode(String token, Instant now) {
        return null;
    }

    @Override
    public void deletePwdAuthCode(String token) {

    }
}
