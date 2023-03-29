package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    List<Comment> findAllByPostIdAndUserId(Long postId, Long userId);

    @Query("select c from Comment c where (c.post.id, c.user.id, c.createdAt) in"
            + "(select c2.post.id, c2.user.id, min(c2.createdAt)"
            + "from Comment c2 group by c2.post.id, c2.user.id)")
    Page<Comment> findAllByUserId(Long userId, Pageable pageable);
}
