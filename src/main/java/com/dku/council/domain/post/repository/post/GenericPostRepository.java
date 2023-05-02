package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GenericPostRepository<T extends Post> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * ACTIVE상태인 post만 가져옵니다.
     */
    @Override
    @Query("select p from Post p " +
            "join fetch p.user u " +
            "join fetch u.major " +
            "where p.id=:id and p.status='ACTIVE' ")
    Optional<T> findById(@Param("id") Long id);

    @Override
    @EntityGraph(attributePaths = {"user", "user.major"})
    Page<T> findAll(Specification<T> spec, Pageable pageable);

    @Query("select p from Post p " +
            "join fetch p.user u " +
            "join fetch u.major " +
            "where p.id=:id and (p.status='BLINDED' or p.status='ACTIVE')")
    Optional<T> findWithBlindedById(@Param("id") Long id);

    @Query("select p from Post p " +
            "where p.id=:id and p.status='BLINDED'")
    Optional<T> findBlindedPostById(@Param("id") Long id);

}