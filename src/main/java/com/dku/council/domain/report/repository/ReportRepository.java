package com.dku.council.domain.report.repository;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.report.model.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Long countByPostId(Long postId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.id IN (SELECT distinct r.post.id FROM Report r) " +
            "and (p.status = 'ACTIVE' or p.status = 'BLINDED')")
    Page<Post> findAllReportedPosts(Pageable pageable);

}
