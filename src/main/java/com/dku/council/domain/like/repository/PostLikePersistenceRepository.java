package com.dku.council.domain.like.repository;

import com.dku.council.domain.like.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikePersistenceRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findAllByPostId(Long postId);
    
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    int countByPostId(Long postId);
}
