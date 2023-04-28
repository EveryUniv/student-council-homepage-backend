package com.dku.council.domain.like.repository.impl;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.repository.LikeMemoryRepository;
import com.dku.council.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dku.council.global.config.redis.RedisKeys.combine;

@Repository
@RequiredArgsConstructor
public class LikeRedisRepository implements LikeMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void like(Long elementId, Long userId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_KEY, target, userId);
        redisTemplate.opsForHash().put(key, elementId.toString(), LikeState.LIKED.toString());

        key = combine(RedisKeys.LIKE_USERS_KEY, target);
        redisTemplate.opsForSet().add(key, userId.toString());
        setIsLiked(elementId, userId, target, true);
    }

    @Override
    public void cancelLike(Long elementId, Long userId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_KEY, target, userId);
        redisTemplate.opsForHash().put(key, elementId.toString(), LikeState.CANCELLED.toString());

        key = combine(RedisKeys.LIKE_USERS_KEY, target);
        redisTemplate.opsForSet().add(key, userId.toString());
        setIsLiked(elementId, userId, target, false);
    }

    @Override
    public void setIsLiked(Long elementId, Long userId, LikeTarget target, boolean isLiked) {
        String key = combine(RedisKeys.LIKE_POSTS_KEY, target, userId);
        String value = LikeState.CANCELLED.toString();
        if (isLiked) {
            value = LikeState.LIKED.toString();
        }
        redisTemplate.opsForHash().put(key, elementId.toString(), value);
    }

    @Override
    public Boolean isLiked(Long elementId, Long userId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_POSTS_KEY, target, userId);
        Object value = redisTemplate.opsForHash().get(key, elementId.toString());
        if (value == null) {
            return null;
        }
        return value.equals(LikeState.LIKED.name());
    }

    @Override
    public int getCachedLikeCount(Long elementId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, target, elementId);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    @Override
    public void setLikeCount(Long elementId, int count, LikeTarget target, Duration expiresAfter) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, target, elementId);
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        redisTemplate.expire(key, expiresAfter);
    }

    @Override
    public void increaseLikeCount(Long elementId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, target, elementId);
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void decreaseLikeCount(Long elementId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, target, elementId);
        redisTemplate.opsForValue().decrement(key);
    }

    @Override
    public List<LikeEntry> getAllLikesAndClear(Long userId, LikeTarget target) {
        String key = combine(RedisKeys.LIKE_KEY, target, userId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        redisTemplate.delete(key);

        key = combine(RedisKeys.LIKE_USERS_KEY, target);
        redisTemplate.opsForSet().remove(key, userId.toString());

        return entries.entrySet().stream()
                .map(entry -> {
                    Long postId = Long.valueOf((String) entry.getKey());
                    LikeState state = LikeState.of((String) entry.getValue());
                    return new LikeEntry(postId, state);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<LikeEntry>> getAllLikesAndClear(LikeTarget target) {
        String key = combine(RedisKeys.LIKE_USERS_KEY, target);
        Set<String> members = redisTemplate.opsForSet().members(key);

        if (members == null) {
            return new HashMap<>();
        }

        return members.stream()
                .map(Long::valueOf)
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> getAllLikesAndClear(userId, target)
                ));
    }
}
