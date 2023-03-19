package com.dku.council.domain.like.repository;

import com.dku.council.domain.like.model.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikePersistenceRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    int countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);
}
