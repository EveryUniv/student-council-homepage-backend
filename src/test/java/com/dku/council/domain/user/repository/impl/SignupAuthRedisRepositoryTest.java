package com.dku.council.domain.user.repository.impl;

import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.global.model.CacheObject;
import com.dku.council.util.ClockUtil;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class SignupAuthRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private SignupAuthRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${app.auth.signup-expires}")
    private Duration expires;

    @Autowired
    private ObjectMapper objectMapper;

    private Instant now;
    private final Clock clock = ClockUtil.create();
    private final String token = "12345";
    private final String auth = "myauth";


    @BeforeEach
    public void setup() {
        now = Instant.now(clock);
    }

    @Test
    @DisplayName("Auth 저장이 잘 되는가")
    void setAuthPayload() throws JsonProcessingException {
        // given
        String key = repository.makeEntryKey(token, auth);
        ClockUtil.create();

        // when
        TestClass data = new TestClass();
        repository.setAuthPayload(token, auth, data, now);

        // then
        Object o = redisTemplate.opsForHash().get(RedisKeys.SIGNUP_AUTH_KEY, key);
        assertThat(getAuth(o).getValue()).isEqualTo(data);
    }

    @Test
    @DisplayName("Auth 중복 저장시 덮어쓰기")
    void setAuthPayloadTwice() throws JsonProcessingException {
        // given
        TestClass data = new TestClass();
        TestClass data2 = new TestClass(5);
        String key = repository.makeEntryKey(token, auth);

        // when
        repository.setAuthPayload(token, auth, data, now);
        repository.setAuthPayload(token, auth, data2, now);

        // then
        Object o = redisTemplate.opsForHash().get(RedisKeys.SIGNUP_AUTH_KEY, key);
        assertThat(getAuth(o).getValue()).isEqualTo(data2);
    }

    @Test
    @DisplayName("Auth 잘 가져와지는가")
    void getAuthPayload() {
        // given
        TestClass data = new TestClass();
        repository.setAuthPayload(token, auth, data, now);

        // when
        Optional<TestClass> result = repository.getAuthPayload(token, auth, TestClass.class, now);

        // then
        assertThat(result.orElseThrow()).isEqualTo(data);
    }

    @Test
    @DisplayName("만료된 경우 안가져와지는가")
    void getAuthPayloadWhenExpires() {
        // given
        TestClass data = new TestClass();
        repository.setAuthPayload(token, auth, data, now);

        // when
        Optional<TestClass> result = repository.getAuthPayload(token, auth, TestClass.class,
                now.plus(expires).plusSeconds(60));

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Auth가 잘 삭제되는가")
    void deleteAuthPayload() {
        // given
        TestClass data = new TestClass();
        repository.setAuthPayload(token, auth, data, now);

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

    private CacheObject<TestClass> getAuth(Object o) throws JsonProcessingException {
        String str = (String) o;
        JavaType type = objectMapper.getTypeFactory().constructParametricType(CacheObject.class, TestClass.class);
        return objectMapper.readValue(str, type);
    }

    private static class TestClass {
        public String stringValue;
        public int intValue;

        public TestClass() {
            this(1);
        }

        public TestClass(int seed) {
            this.stringValue = "test" + seed;
            this.intValue = 17 + seed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestClass testClass = (TestClass) o;
            return intValue == testClass.intValue && Objects.equals(stringValue, testClass.stringValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stringValue, intValue);
        }
    }
}