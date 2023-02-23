package com.dku.council.domain.like.repository.impl;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostLikeRedisRepository implements PostLikeMemoryRepository {

    public static final String POST_LIKE_KEY = "PostLike";
    public static final String POST_LIKE_COUNT_KEY = "PostLikeCount";
    public static final String KEY_DELIMITER = ":";

    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void addPostLike(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        redisTemplate.opsForHash().put(POST_LIKE_KEY, key, LikeState.LIKED.ordinal());
    }

    @Override
    public void removePostLike(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        redisTemplate.opsForHash().put(POST_LIKE_KEY, key, LikeState.CANCELLED.ordinal());
    }

    @Override
    public Boolean isPostLiked(Long postId, Long userId) {
        String key = makeEntryKey(postId, userId);
        Object value = redisTemplate.opsForHash().get(POST_LIKE_KEY, key);
        if (value == null) {
            return null;
        }
        return (int) value == LikeState.LIKED.ordinal();
    }

    @Override
    public int getCachedLikeCount(Long postId) {
        Object value = redisTemplate.opsForHash().get(POST_LIKE_COUNT_KEY, postId);
        if (value == null) {
            return -1;
        }
        return (int) value;
    }

    @Override
    public void setLikeCount(Long postId, int count) {
        redisTemplate.opsForHash().put(POST_LIKE_COUNT_KEY, postId, count);
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

        try (Cursor<Map.Entry<Object, Object>> cursor = op.scan(POST_LIKE_KEY, ScanOptions.NONE)) {
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();

                String key = (String) entry.getKey();
                String[] keyValue = key.split(KEY_DELIMITER);
                Long postId = Long.valueOf(keyValue[0]);
                Long userId = Long.valueOf(keyValue[1]);
                LikeState state = LikeState.values()[(int) entry.getValue()];

                result.add(new LikeEntry(postId, userId, state));
                if (clear) {
                    op.delete(POST_LIKE_KEY, key);
                }
            }
        }

        return result;
    }

    public String makeEntryKey(Long postId, Long userId) {
        return postId.toString() + KEY_DELIMITER + userId;
    }
}
