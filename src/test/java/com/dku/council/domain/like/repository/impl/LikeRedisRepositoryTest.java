package com.dku.council.domain.like.repository.impl;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.global.config.redis.RedisKeys;
import com.dku.council.util.base.AbstractContainerRedisTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static com.dku.council.global.config.redis.RedisKeys.combine;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@FullIntegrationTest
class LikeRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private LikeRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    @DisplayName("like 엔티디가 잘 추가되는가?")
    void like() {
        // given
        PostLikeKey key = new PostLikeKey();

        // when
        repository.like(key.elementId, key.userId, POST);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.LIKED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(true);
    }

    @Test
    @DisplayName("중복 추가시 덮어쓰기")
    void addDuplicatedPostLike() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.putLike(redisTemplate, "0");

        // when
        repository.like(key.elementId, key.userId, POST);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.LIKED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 있을 때")
    void cancelLike() {
        // given
        PostLikeKey key = new PostLikeKey();
        repository.like(key.elementId, key.userId, POST);

        // when
        repository.cancelLike(key.elementId, key.userId, POST);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.CANCELLED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 없을 때")
    void cancelLikeWhenNoEntry() {
        // given
        PostLikeKey key = new PostLikeKey();

        // when
        repository.cancelLike(key.elementId, key.userId, POST);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.CANCELLED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 있는 경우")
    void isPostLiked() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.setLiked(redisTemplate, true);

        // when
        Boolean value = repository.isLiked(key.elementId, key.userId, POST);

        // then
        assertThat(value).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 취소된 경우")
    void isPostLikedCancelled() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.setLiked(redisTemplate, false);

        // when
        Boolean value = repository.isLiked(key.elementId, key.userId, POST);

        // then
        assertThat(value).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 캐싱이 안된 경우")
    void isPostLikedNoEntity() {
        // given
        PostLikeKey key = new PostLikeKey();

        // when
        Boolean value = repository.isLiked(key.elementId, key.userId, POST);

        // then
        assertThat(value).isEqualTo(null);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱된 경우")
    void getCachedLikeCount() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.setCount(redisTemplate, "3");

        // when
        int count = repository.getCachedLikeCount(key.elementId, POST);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱안된 경우")
    void getCachedLikeCountNoCached() {
        // given
        PostLikeKey key = new PostLikeKey();

        // when
        int count = repository.getCachedLikeCount(key.elementId, POST);

        // then
        assertThat(count).isEqualTo(-1);
    }

    @Test
    @DisplayName("좋아요 수 직접 캐싱")
    void setLikeCount() {
        // given
        PostLikeKey key = new PostLikeKey();

        // when
        repository.setLikeCount(key.elementId, 8, POST, Duration.ofHours(1));

        // then
        assertThat(key.getCount(redisTemplate)).isEqualTo("8");
    }

    @Test
    @DisplayName("좋아요 수 증가")
    void increaseLikes() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.setCount(redisTemplate, "10");

        // when
        repository.increaseLikeCount(key.elementId, POST);

        // then
        assertThat(key.getCount(redisTemplate)).isEqualTo("11");
    }

    @Test
    @DisplayName("좋아요 수 감소")
    void decreaseLikes() {
        // given
        PostLikeKey key = new PostLikeKey();
        key.setCount(redisTemplate, "10");

        // when
        repository.decreaseLikeCount(key.elementId, POST);

        // then
        assertThat(key.getCount(redisTemplate)).isEqualTo("9");
    }

    @Test
    @DisplayName("특정 유저의 캐싱된 모든 좋아요 가져오고 삭제가 잘 되는지?")
    void getAllLikesAndClearForUser() {
        // given
        final int count = 10;
        for (int i = 0; i < count; i++) {
            PostLikeKey key = new PostLikeKey(i + 100L, 100L);
            repository.like(key.elementId, key.userId, POST);
        }

        // when
        List<LikeEntry> likes = repository.getAllLikesAndClear(100L, POST);

        // then
        String key = combine(RedisKeys.LIKE_KEY, POST, 100L);
        Long size = redisTemplate.opsForHash().size(key);
        assertThat(size).isEqualTo(0);

        key = combine(RedisKeys.LIKE_USERS_KEY, POST);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, "100");
        assertThat(isMember).isEqualTo(false);

        assertThat(likes.size()).isEqualTo(count);
    }

    @Test
    @DisplayName("캐싱된 모든 좋아요 가져오고 삭제가 잘 되는지?")
    void getAllPostLikesAndClear() {
        // given
        final int count = 10;
        for (int i = 0; i < count; i++) {
            PostLikeKey key = new PostLikeKey(i + 100L, i + 100L);
            repository.like(key.elementId, key.userId, POST);
        }

        // when
        Map<Long, List<LikeEntry>> likes = repository.getAllLikesAndClear(POST);

        // then
        String key = combine(RedisKeys.LIKE_USERS_KEY, POST);
        Long size = redisTemplate.opsForHash().size(key);
        assertThat(size).isEqualTo(0);

        for (int i = 0; i < count; i++) {
            key = combine(RedisKeys.LIKE_KEY, POST, i + 100L);
            size = redisTemplate.opsForHash().size(key);
            assertThat(size).isEqualTo(0);
        }

        assertThat(likes.size()).isEqualTo(count);
    }

    private static class PostLikeKey {
        private static final Random RAND = new Random();
        private final Long elementId;
        private final Long userId;
        private final String keyString;

        public PostLikeKey() {
            this(RAND.nextLong(), RAND.nextLong());
        }

        public PostLikeKey(Long elementId, Long userId) {
            this.elementId = elementId;
            this.userId = userId;
            this.keyString = RedisKeys.combine(RedisKeys.LIKE_KEY, POST, userId);
        }

        public Object getLike(StringRedisTemplate redisTemplate) {
            return redisTemplate.opsForHash().get(keyString, elementId.toString());
        }

        public void putLike(StringRedisTemplate redisTemplate, String data) {
            redisTemplate.opsForHash().put(keyString, elementId.toString(), data);
        }

        public Object getCount(StringRedisTemplate redisTemplate) {
            String key = combine(RedisKeys.LIKE_COUNT_KEY, POST, elementId);
            return redisTemplate.opsForValue().get(key);
        }

        public void setCount(StringRedisTemplate redisTemplate, String data) {
            String key = combine(RedisKeys.LIKE_COUNT_KEY, POST, elementId);
            redisTemplate.opsForValue().set(key, data);
        }

        public boolean isLiked(StringRedisTemplate redisTemplate) {
            String key = combine(RedisKeys.LIKE_POSTS_KEY, POST, userId);
            Object value = redisTemplate.opsForHash().get(key, elementId.toString());
            return LikeState.LIKED.name().equals(value);
        }

        public void setLiked(StringRedisTemplate redisTemplate, boolean isLiked) {
            String key = combine(RedisKeys.LIKE_POSTS_KEY, POST, userId);
            String value = LikeState.CANCELLED.name();
            if (isLiked) {
                value = LikeState.LIKED.name();
            }
            redisTemplate.opsForHash().put(key, elementId.toString(), value);
        }
    }
}