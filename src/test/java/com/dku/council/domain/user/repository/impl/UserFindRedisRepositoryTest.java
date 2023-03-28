package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.model.SMSAuth;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.util.ClockUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFindRedisRepositoryTest {

    private final Clock clock = ClockUtil.create();

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashOperations<String, Object, Object> ops;

    @Mock
    private TypeFactory typeFactory;

    private UserFindRedisRepository repository;

    @BeforeEach
    public void setup() {
        repository = new UserFindRedisRepository(redisTemplate, objectMapper, Duration.ofDays(1));
    }

    @Test
    @DisplayName("Auth 저장이 잘 되는가")
    void setPwdAuthCode() throws JsonProcessingException {
        // given
        Instant now = Instant.now(clock);
        when(redisTemplate.opsForHash()).thenReturn(ops);
        when(objectMapper.writeValueAsString(argThat(arg -> {
            SMSAuth auth = ((CacheObject<SMSAuth>) arg).getValue();
            return auth.getCode().equals("code") && auth.getPhone().equals("phone");
        }))).thenReturn("json");

        // when
        repository.setAuthCode("token", "code", "phone", now);

        // then
        verify(ops).put(
                eq(RedisKeys.USER_FIND_AUTH_KEY),
                eq("token"),
                eq("json"));
    }

    @Test
    @DisplayName("Auth 조회가 잘 되는가")
    void getPwdAuthCode() throws JsonProcessingException {
        // given
        Instant now = Instant.now(clock);
        String token = "token";
        String json = "json";
        SMSAuth expected = new SMSAuth("code", "phone");
        CacheObject<SMSAuth> obj = new CacheObject<>(now.plusSeconds(1), expected);

        when(redisTemplate.opsForHash()).thenReturn(ops);
        when(ops.get(RedisKeys.USER_FIND_AUTH_KEY, token)).thenReturn(json);
        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructType((Class) any())).thenReturn(null);
        when(objectMapper.readValue(eq(json), (JavaType) isNull())).thenReturn(obj);

        // when
        Optional<SMSAuth> auth = repository.getAuthCode(token, now);

        // then
        assertThat(auth.orElseThrow()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Auth 삭제가 잘 되는가")
    void deletePwdAuthCode() {
        // given
        String token = "token";
        when(redisTemplate.opsForHash()).thenReturn(ops);

        // when
        repository.remove(token);

        // then
        verify(ops).delete(RedisKeys.USER_FIND_AUTH_KEY, token);
    }
}