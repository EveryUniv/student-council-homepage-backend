package com.dku.council.domain.statistic.repository;

import com.dku.council.domain.statistic.PetitionStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetitionStatisticRepository extends JpaRepository<PetitionStatistic, Long> {
    List<PetitionStatistic> findAllByPetition(Long petitionId);
}
