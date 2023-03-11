package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.global.config.redis.RedisKeys;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SignupAuthRedisRepository implements SignupAuthRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void setAuthPayload(String signupToken, String authName, Object data) {
        String key = makeEntryKey(signupToken, authName);
        try {
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForHash().put(RedisKeys.SIGNUP_AUTH_KEY, key, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Optional<T> getAuthPayload(String signupToken, String authName, Class<T> payloadClass) {
        String key = makeEntryKey(signupToken, authName);
        Object value = redisTemplate.opsForHash().get(RedisKeys.SIGNUP_AUTH_KEY, key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            T result = objectMapper.readValue((String) value, payloadClass);
            return Optional.of(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAuthPayload(String signupToken, String authName) {
        String key = makeEntryKey(signupToken, authName);
        return redisTemplate.opsForHash().delete(RedisKeys.SIGNUP_AUTH_KEY, key) > 0;
    }

    public String makeEntryKey(String signupToken, String authName) {
        return signupToken + RedisKeys.KEY_DELIMITER + authName;
    }
}
