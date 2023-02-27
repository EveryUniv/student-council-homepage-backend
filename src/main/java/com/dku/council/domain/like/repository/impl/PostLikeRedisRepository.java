package com.dku.council.domain.like.repository.impl;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostLikeRedisRepository implements PostLikeMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void addPostLike(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        redisTemplate.opsForHash().put(RedisKeys.POST_LIKE_KEY, key, LikeState.LIKED.name());
    }

    @Override
    public void removePostLike(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        redisTemplate.opsForHash().put(RedisKeys.POST_LIKE_KEY, key, LikeState.CANCELLED.name());
    }

    @Override
    public Boolean isPostLiked(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_KEY, key);
        if (value == null) {
            return null;
        }
        return value.equals(LikeState.LIKED.name());
    }

    @Override
    public int getCachedLikeCount(Long postId) {
        Object value = redisTemplate.opsForHash().get(RedisKeys.POST_LIKE_COUNT_KEY, postId.toString());
        if (value == null) {
            return -1;
        }
        return Integer.parseInt((String) value);
    }

    @Override
    public void setLikeCount(Long postId, int count) {
        redisTemplate.opsForHash().put(RedisKeys.POST_LIKE_COUNT_KEY, postId.toString(), String.valueOf(count));
    }

    @Override
    public List<LikeEntry> getAllPostLikes() {
        return getAllPostLikes(false);
    }

    @Override
    public List<LikeEntry> getAllPostLikesAndClear() {
        return getAllPostLikes(true);
    }

    private List<LikeEntry> getAllPostLikes(boolean clear) {
        List<LikeEntry> result = new ArrayList<>();
        HashOperations<String, Object, Object> op = redisTemplate.opsForHash();

        try (Cursor<Map.Entry<Object, Object>> cursor = op.scan(RedisKeys.POST_LIKE_KEY, ScanOptions.NONE)) {
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();

                String key = (String) entry.getKey();
                String[] keyValue = key.split(RedisKeys.KEY_DELIMITER);
                Long postId = Long.valueOf(keyValue[0]);
                Long userId = Long.valueOf(keyValue[1]);
                LikeState state = LikeState.of((String) entry.getValue());

                result.add(new LikeEntry(postId, userId, state));
                if (clear) {
                    op.delete(RedisKeys.POST_LIKE_KEY, key);
                }
            }
        }

        return result;
    }

    public String makeEntryKey(Long postId, Long userId) {
        return postId.toString() + RedisKeys.KEY_DELIMITER + userId;
    }
}
