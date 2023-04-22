package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PetitionRepository extends GenericPostRepository<Petition>, JpaSpecificationExecutor<Petition> {
    @Query("select p from Petition p " +
            "where p.status != 'BLINDED' " +
            "order by p.createdAt desc")
    List<Petition> findTopByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Petition p set p.extraStatus = 'EXPIRED' " +
            "where p.extraStatus = 'ACTIVE' " +
            "and p.createdAt <= :lessThanCreatedAt")
    void updateExpiredPetition(LocalDateTime lessThanCreatedAt);

    @Override
    @EntityGraph(attributePaths = {"user", "user.major"})
    Page<Post> findAll(Specification spec, Pageable pageable);
}