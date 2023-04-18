package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
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

    @Query("select p from Post p where p.id in " +
            "(select p.id from Post p " +
            "join Comment c " +
            "on p.id = c.post.id and c.user.id=:userId and c.status='ACTIVE' " +
            "group by p.id)")
    Page<Post> findAllCommentByUserId(Long userId, Pageable pageable);

    /**
     * 관리자만 사용 가능합니다.
     */
    @Query("select c from Comment c where c.user.id=:userId order by c.createdAt desc")
    List<Comment> findAllByUserId(Long userId);
}
