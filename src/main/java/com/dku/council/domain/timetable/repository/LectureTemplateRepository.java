package com.dku.council.domain.timetable.repository;

import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LectureTemplateRepository extends JpaRepository<LectureTemplate, Long>, JpaSpecificationExecutor<LectureTemplate> {
}
