package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

// TODO 단건조회시 한방 쿼리로 가져오기
public interface GenericPostRepository<T extends Post> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    /**
     * ACTIVE상태인 post만 가져옵니다.
     */
    @Query("select p from Post p where p.id=:id and p.status='ACTIVE'")
    Optional<T> findById(Long id);
}