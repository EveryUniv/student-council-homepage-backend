package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.TimeTable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    List<TimeTable> findAllByUserId(Long userId);

    @Override
    @EntityGraph(attributePaths = {"schedules"})
    Optional<TimeTable> findById(Long id);
}
