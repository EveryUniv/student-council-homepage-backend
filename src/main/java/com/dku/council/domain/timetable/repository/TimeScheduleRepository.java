package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeScheduleRepository extends JpaRepository<TimeSchedule, Long> {
}
