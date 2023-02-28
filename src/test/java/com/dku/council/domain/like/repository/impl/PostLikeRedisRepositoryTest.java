package com.dku.council.domain.like.repository.impl;

import com.dku.council.common.AbstractContainerRedisTest;
import com.dku.council.common.OnlyDevTest;
import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.global.config.redis.RedisKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@OnlyDevTest
class PostLikeRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private PostLikeRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @BeforeEach
    void setup() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null) {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
        }
    }

    @Test
    @DisplayName("like 엔티디가 잘 추가되는가?")
    void addPostLike() {
        // given
        PostLikeKey key = new PostLikeKey(repository);

        // when
        repository.addPostLike(key.postId, key.userId);

        // then
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_KEY, key.toString());
        assertThat(value).isEqualTo(LikeState.LIKED.name());
    }

    @Test
    @DisplayName("중복 추가시 덮어쓰기")
    void addDuplicatedPostLike() {
        // given
        PostLikeKey key = new PostLikeKey(repository);
        redisTemplate.opsForHash().put(RedisKeys.POST_LIKE_KEY, key.toString(), "0");

        // when
        repository.addPostLike(key.postId, key.userId);

        // then
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_KEY, key.toString());
        assertThat(value).isEqualTo(LikeState.LIKED.name());
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 있을 때")
    void removePostLike() {
        // given
        PostLikeKey key = new PostLikeKey(repository);
        repository.addPostLike(key.postId, key.userId);

        // when
        repository.removePostLike(key.postId, key.userId);

        // then
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_KEY, key.toString());
        assertThat(value).isEqualTo(LikeState.CANCELLED.name());
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 없을 때")
    void removePostLikeWhenNoEntry() {
        // given
        PostLikeKey key = new PostLikeKey(repository);

        // when
        repository.removePostLike(key.postId, key.userId);

        // then
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_KEY, key.toString());
        assertThat(value).isEqualTo(LikeState.CANCELLED.name());
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 있는 경우")
    void isPostLiked() {
        // given
        PostLikeKey key = new PostLikeKey(repository);
        repository.addPostLike(key.postId, key.userId);

        // when
        Boolean value = repository.isPostLiked(key.postId, key.userId);

        // then
        assertThat(value).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 취소된 경우")
    void isPostLikedCancelled() {
        // given
        PostLikeKey key = new PostLikeKey(repository);
        repository.removePostLike(key.postId, key.userId);

        // when
        Boolean value = repository.isPostLiked(key.postId, key.userId);

        // then
        assertThat(value).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 캐싱이 안된 경우")
    void isPostLikedNoEntity() {
        // given
        PostLikeKey key = new PostLikeKey(repository);

        // when
        Boolean value = repository.isPostLiked(key.postId, key.userId);

        // then
        assertThat(value).isEqualTo(null);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱된 경우")
    void getCachedLikeCount() {
        // given
        PostLikeKey key = new PostLikeKey(repository);
        redisTemplate.opsForHash().put(RedisKeys.POST_LIKE_COUNT_KEY, key.postId.toString(), "3");

        // when
        int count = repository.getCachedLikeCount(key.postId);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱안된 경우")
    void getCachedLikeCountNoCached() {
        // given
        PostLikeKey key = new PostLikeKey(repository);

        // when
        int count = repository.getCachedLikeCount(key.postId);

        // then
        assertThat(count).isEqualTo(-1);
    }

    @Test
    @DisplayName("좋아요 수 직접 캐싱")
    void setLikeCount() {
        // given
        PostLikeKey key = new PostLikeKey(repository);

        // when
        repository.setLikeCount(key.postId, 8);

        // then
        Object count = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_COUNT_KEY, String.valueOf(key.postId));
        assertThat(count).isEqualTo("8");
    }

    @Test
    @DisplayName("캐싱된 모든 좋아요 가져오기")
    void getAllPostLikes() {
        // given
        final int count = 10;
        PostLikeKey[] keys = new PostLikeKey[count];
        for (int i = 0; i < count; i++) {
            keys[i] = new PostLikeKey(repository, i + 100L, i + 100L);
            if (i > count / 2) {
                repository.removePostLike(keys[i].postId, keys[i].userId);
            } else {
                repository.addPostLike(keys[i].postId, keys[i].userId);
            }
        }

        // when
        List<LikeEntry> likes = repository.getAllPostLikes();

        // then
        Long size = redisTemplate.opsForHash().size(RedisKeys.POST_LIKE_KEY);
        LikeEntry[] expected = new LikeEntry[count];
        for (int i = 0; i < count; i++) {
            expected[i] = new LikeEntry(keys[i].postId, keys[i].userId,
                    i > count / 2 ? LikeState.CANCELLED : LikeState.LIKED);
        }

        assertThat(likes).containsExactlyInAnyOrder(expected);
        assertThat(size).isEqualTo(likes.size());
    }

    @Test
    @DisplayName("캐싱된 모든 좋아요 가져오고 삭제가 잘 되는지?")
    void getAllPostLikesAndClear() {
        // given
        final int count = 10;
        for (int i = 0; i < count; i++) {
            PostLikeKey key = new PostLikeKey(repository, i + 100L, i + 100L);
            repository.addPostLike(key.postId, key.userId);
        }

        // when
        List<LikeEntry> likes = repository.getAllPostLikesAndClear();

        // then
        Long size = redisTemplate.opsForHash().size(RedisKeys.POST_LIKE_KEY);
        assertThat(likes.size()).isEqualTo(count);
        assertThat(size).isEqualTo(0);
    }

    private static class PostLikeKey {
        private static final Random RAND = new Random();
        private final Long postId;
        private final Long userId;
        private final String keyString;

        public PostLikeKey(PostLikeRedisRepository repository) {
            this(repository, RAND.nextLong(), RAND.nextLong());
        }

        public PostLikeKey(PostLikeRedisRepository repository, Long postId, Long userId) {
            this.postId = postId;
            this.userId = userId;
            this.keyString = repository.makeEntryKey(postId, userId);
        }

        public String toString() {
            return keyString;
        }
    }
}