package com.dku.council.global.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis Connection을 확인해보기위해 만든 더미 테스트
 * 실제 CI에 사용될 테스트는 아니다.
 */
class RedisConnectionTest {

    private StringRedisTemplate redisTemplate;

    @BeforeEach
    public void init() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
        LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(configuration);
        redisConnectionFactory.afterPropertiesSet();
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Test
    @DisplayName("Redis Connection을 가지고 놀아보는 놀이터")
    @Disabled
    public void connectionPlayground() {
        redisTemplate.opsForSet().add("like:post:1", "user_1");
        redisTemplate.opsForSet().add("like:post:1", "user_2");
        redisTemplate.opsForSet().add("like:post:1", "user_3");
        redisTemplate.opsForSet().add("like:post:2", "user_2");
        redisTemplate.opsForSet().add("like:post:2", "user_3");
    }
}