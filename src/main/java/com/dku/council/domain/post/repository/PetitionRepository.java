package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PetitionRepository extends GenericPostRepository<Petition> {
    List<Petition> findTop5ByOrderByCreatedAtDesc();

    @Modifying(clearAutomatically = true)
    @Query("update Petition p set p.extraStatus = 'EXPIRED' " +
            "where p.extraStatus = 'ACTIVE' " +
            "and p.createdAt <= :lessThanCreatedAt")
    void updateExpiredPetition(LocalDateTime lessThanCreatedAt);
}