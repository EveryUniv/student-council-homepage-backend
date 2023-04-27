package com.dku.council.domain.mainpage.repository;

import com.dku.council.domain.mainpage.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from Schedule s " +
            "where s.startDate between :startDate and :endDate or " +
            "s.endDate between :startDate and :endDate")
    List<Schedule> findAllOverlapped(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("delete from Schedule s " +
            "where s.startDate between :startDate and :endDate or " +
            "s.endDate between :startDate and :endDate")
    void deleteAllOverlapped(@Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
}
