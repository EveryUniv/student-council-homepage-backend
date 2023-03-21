package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.entity.CommentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLogRepository extends JpaRepository<CommentLog, Long> {
}
