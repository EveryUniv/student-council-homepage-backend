package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenericPostRepository<T extends Post> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    /**
     * ACTIVE상태인 post만 가져옵니다.
     */
    @Override
    @Query("select p from Post p where p.id=:id and p.status='ACTIVE'")
    Optional<T> findById(Long id);

    @Query("select p from Post p where p.id=:id and (p.status='BLINDED' or p.status='ACTIVE')")
    Optional<T> findActiveAndBlindedPostById(Long id);

    @Query("select p from Post p where p.id=:id and p.status='BLINDED'")
    Optional<T> findBlindedPostById(Long id);

}