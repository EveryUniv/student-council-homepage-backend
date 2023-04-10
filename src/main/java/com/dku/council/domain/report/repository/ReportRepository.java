package com.dku.council.domain.report.repository;

import com.dku.council.domain.report.model.dto.response.SummarizedReportedPostDto;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    Long countByPostId(Long postId);

    @Query("SELECT r FROM Report r " +
            "WHERE r.id NOT IN (" +
            "    SELECT r2.id FROM Report r2 " +
            "    WHERE r2.post.id IN (" +
            "        SELECT r3.post.id FROM Report r3 " +
            "        GROUP BY r3.post.id " +
            "        HAVING COUNT(r3.post.id) > 1" +
            "    ) AND r2.id <> (" +
            "        SELECT MIN(r4.id) FROM Report r4 " +
            "        WHERE r4.post.id = r2.post.id" +
            "    )" +
            ") and (r.post.status = 'ACTIVE' or r.post.status = 'BLINDED')")
    Page<Report> findAllReportedPosts(Pageable pageable);

}
