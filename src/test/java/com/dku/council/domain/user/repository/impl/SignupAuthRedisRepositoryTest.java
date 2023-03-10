package com.dku.council.domain.user.repository.impl;

import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.util.FullIntegrationTest;
import com.dku.council.util.base.AbstractContainerRedisTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class SignupAuthRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private SignupAuthRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String token = "12345";
    private final String auth = "myauth";

    @Test
    @DisplayName("Auth 저장이 잘 되는가")
    void setAuthPayload() {
        // given
        String key = repository.makeEntryKey(token, auth);

        // when
        repository.setAuthPayload(token, auth, "MyData");

        // then
        Object o = redisTemplate.opsForHash().get(RedisKeys.SIGNUP_AUTH_KEY, key);
        assertThat(o).isEqualTo("\"MyData\"");
    }

    @Test
    @DisplayName("Auth 중복 저장시 덮어쓰기")
    void setAuthPayloadTwice() {
        // given
        String key = repository.makeEntryKey(token, auth);

        // when
        repository.setAuthPayload(token, auth, "MyData");
        repository.setAuthPayload(token, auth, "MyData22");

        // then
        Object o = redisTemplate.opsForHash().get(RedisKeys.SIGNUP_AUTH_KEY, key);
        assertThat(o).isEqualTo("\"MyData22\"");
    }

    @Test
    @DisplayName("Auth 잘 가져와지는가")
    void getAuthPayload() {
        // given
        repository.setAuthPayload(token, auth, "MyData");

        // when
        Optional<String> data = repository.getAuthPayload(token, auth, String.class);

        // then
        assertThat(data.orElseThrow()).isEqualTo("MyData");
    }

    @Test
    @DisplayName("Auth가 잘 삭제되는가")
    void deleteAuthPayload() {
        // given
        repository.setAuthPayload(token, auth, "MyData");

        // when
        boolean result = repository.deleteAuthPayload(token, auth);

        // then
        Long size = redisTemplate.opsForHash().size(RedisKeys.SIGNUP_AUTH_KEY);
        assertThat(size).isEqualTo(0);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Auth가 없을 때 삭제 안되야 함")
    void deleteAuthPayloadWhenNotFound() {
        // when
        boolean result = repository.deleteAuthPayload(token, auth);

        // then
        assertThat(result).isFalse();
    }
}