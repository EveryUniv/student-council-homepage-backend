package com.dku.council.domain.like.repository.impl;

import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostLikeRedisRepository implements PostLikeMemoryRepository {

    @Override
    public void addLikeMemberAt(Long postId, Long userId) {

    }

    @Override
    public void removeLikeMemberAt(Long postId, Long userId) {

    }

    @Override
    public boolean containsLikeMemberAt(Long postId, Long userId) {
        return false;
    }

    @Override
    public boolean isCachedPost(Long postId) {
        return false;
    }

    @Override
    public void setLikeMembers(Long postId, List<Long> members) {

    }

    @Override
    public int getCachedLikeCount(Long postId) {
        return 0;
    }

    @Override
    public void increaseLikeCount(Long postId) {

    }

    @Override
    public void decreaseLikeCount(Long postId) {

    }

    @Override
    public void setLikeCount(Long postId, int count) {

    }
}
