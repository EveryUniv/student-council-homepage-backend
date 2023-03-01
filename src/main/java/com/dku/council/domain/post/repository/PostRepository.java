package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * ACTIVE상태인 post만 가져옵니다.
     */
    @Query("select p from Post p where p.id = :id and p.status = 'ACTIVE'")
    Optional<Post> findByIdAndActived(Long id);

    /**
     * @param id must not be {@literal null}.
     * @deprecated active되지 않은 게시글까지 가져옵니다. findByIdAndActived를 사용하세요.
     */
    @Deprecated
    @Override
    Optional<Post> findById(Long id);
}