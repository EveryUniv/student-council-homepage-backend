package com.dku.council.domain.like.service.impl;

import com.dku.council.domain.like.model.entity.PostLike;
import com.dku.council.domain.like.repository.PostLikePersistenceRepository;
import com.dku.council.domain.like.service.PostLikeService;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DBPostLikeServiceImpl implements PostLikeService {
    private final PostLikePersistenceRepository persistenceRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    @Transactional
    public void like(Long postId, Long userId) {
        if (!isPostLiked(postId, userId)) {
            User user = userRepository.getReferenceById(userId);
            Post post = postRepository.getReferenceById(postId);
            persistenceRepository.save(new PostLike(user, post));
        }
    }

    @Transactional
    public void cancelLike(Long postId, Long userId) {
        if (isPostLiked(postId, userId)) {
            persistenceRepository.deleteByPostIdAndUserId(postId, userId);
        }
    }

    @Override
    public boolean isPostLiked(Long postId, Long userId) {
        return persistenceRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    @Transactional(readOnly = true)
    public int getCountOfLikes(Long postId) {
        return persistenceRepository.countByPostId(postId);
    }
}
