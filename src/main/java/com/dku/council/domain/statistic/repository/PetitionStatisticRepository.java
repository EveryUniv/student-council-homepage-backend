package com.dku.council.domain.statistic.repository;

import com.dku.council.domain.statistic.PetitionStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetitionStatisticRepository extends JpaRepository<PetitionStatistic, Long> {
    List<PetitionStatistic> findAllByPetitionId(Long petitionId);

    Integer countByPetitionId(Long petitionId);

    Optional<PetitionStatistic> findByPetitionIdAndUserId(Long petitionId, Long userId);
}
