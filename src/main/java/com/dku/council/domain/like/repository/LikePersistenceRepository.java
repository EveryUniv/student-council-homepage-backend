package com.dku.council.domain.like.repository;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.model.entity.LikeElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikePersistenceRepository extends JpaRepository<LikeElement, Long> {

    @Query("select l from LikeElement l " +
            "where l.elementId = :elementId " +
            "and l.user.id = :userId " +
            "and l.target = :target")
    Optional<LikeElement> findByElementIdAndUserId(@Param("elementId") Long elementId,
                                                   @Param("userId") Long userId,
                                                   @Param("target") LikeTarget target);

    int countByElementIdAndTarget(Long elementId, LikeTarget target);

    @Modifying
    @Query("delete from LikeElement l " +
            "where l.elementId = :elementId " +
            "and l.user.id = :userId " +
            "and l.target = :target")
    void deleteByElementIdAndUserId(@Param("elementId") Long elementId,
                                    @Param("userId") Long userId,
                                    @Param("target") LikeTarget target);

    @Query("select l from LikeElement l " +
            "where l.user.id = :userId and l.target = :target")
    Page<LikeElement> findAllByUserId(@Param("userId") Long userId,
                                      @Param("target") LikeTarget target,
                                      Pageable pageable);

    @Query("select count(*) from LikeElement l " +
            "join Post p on l.elementId = p.id and p.status = 'ACTIVE' " +
            "and l.user.id = :userId and l.target = :target")
    Long countPostByUserId(@Param("userId") Long userId,
                           @Param("target") LikeTarget target);

}
