package com.dku.council.domain.schedule.repository;

import com.dku.council.domain.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from Schedule s " +
            "where s.startDateTime between :startDate and :endDate or " +
            "s.endDateTime between :startDate and :endDate")
    List<Schedule> findAllOverlapped(LocalDate startDate, LocalDate endDate);
}
