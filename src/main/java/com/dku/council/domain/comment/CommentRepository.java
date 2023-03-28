package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "where c.post.id=:postId " +
            "and (c.status='ACTIVE' or c.status='EDITED')")
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    @Query("select c from Comment c " +
            "where c.post.id=:postId " +
            "and c.user.id=:userId " +
            "and (c.status='ACTIVE' or c.status='EDITED')")
    List<Comment> findAllByPostIdAndUserId(Long postId, Long userId);

    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
}
