package com.dku.council.domain.report.repository;

import com.dku.council.domain.report.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(Long postId);
}
