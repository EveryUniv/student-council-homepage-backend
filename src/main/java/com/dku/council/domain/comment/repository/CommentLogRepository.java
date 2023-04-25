package com.dku.council.domain.comment.repository;

import com.dku.council.domain.comment.model.entity.CommentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLogRepository extends JpaRepository<CommentLog, Long> {
}
