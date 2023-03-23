package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    List<TimeTable> findAllByUserId(Long userId);
}
