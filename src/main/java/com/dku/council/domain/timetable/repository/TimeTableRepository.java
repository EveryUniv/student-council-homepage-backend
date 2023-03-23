package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
}
