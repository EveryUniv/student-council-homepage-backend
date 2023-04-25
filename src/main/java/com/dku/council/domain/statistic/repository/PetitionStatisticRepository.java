package com.dku.council.domain.statistic.repository;

import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetitionStatisticRepository extends JpaRepository<PetitionStatistic, Long> {
    @Query("select new com.dku.council.domain.statistic.model.dto.PetitionStatisticDto(department, count(department)) " +
            "from PetitionStatistic " +
            "where petition.id=:petitionId " +
            "group by department " +
            "order by count(department) desc")
    List<PetitionStatisticDto> findCountGroupByDepartment(@Param("petitionId") Long petitionId, Pageable pageable);

    Integer countByPetitionId(Long petitionId);

    @Query("select count(*) " +
            "from PetitionStatistic " +
            "where petition.id=:petitionId and user.id=:userId")
    Long countByPetitionIdAndUserId(@Param("petitionId") Long petitionId, @Param("userId") Long userId);
}
