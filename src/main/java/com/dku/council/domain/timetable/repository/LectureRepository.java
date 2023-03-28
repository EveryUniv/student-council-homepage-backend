package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
