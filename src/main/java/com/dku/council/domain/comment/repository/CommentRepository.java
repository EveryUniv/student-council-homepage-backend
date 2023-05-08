package com.dku.council.domain.comment.repository;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c " +
            "where c.post.id=:postId and c.post.status='ACTIVE' " +
            "and (c.status='ACTIVE' or c.status='EDITED') ")
    Page<Comment> findAllByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("select c from Comment c " +
            "where c.post.id=:postId and c.post.status='ACTIVE' " +
            "and c.user.id=:userId " +
            "and (c.status='ACTIVE' or c.status='EDITED') ")
    List<Comment> findAllByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("select distinct p from Post p " +
            "join Comment c " +
            "on p.id = c.post.id and c.user.id=:userId and p.status='ACTIVE' and " +
                "(c.status='ACTIVE' or c.status='EDITED') ")
    Page<Post> findAllCommentedByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 활성 상태와 상관없이 모든 유저의 댓글을 가져옵니다. 관리자만 사용 가능합니다.
     */
    @Query("select c from Comment c where c.user.id=:userId")
    Page<Comment> findAllByUserIdWithAdmin(@Param("userId") Long userId, Pageable pageable);

    /**
     * User가 댓글을 작성한 게시글의 개수를 가져옵니다.
     */
    @Query("select count(distinct p) from Post p " +
            "join Comment c " +
            "on p.id = c.post.id and c.user.id=:userId and p.status='ACTIVE' and " +
                "(c.status='ACTIVE' or c.status='EDITED') ")
    Long countAllCommentedByUserId(@Param("userId") Long userId);

    /**
     * 탈퇴한 회원의 데이터를 바꾸기 위해 활성화 여부와 상관없이 작성자의 모든 댓글을 가져옵니다.
     */
    @Query("select c from Comment c where c.user.id=:userId")
    List<Comment> findAllByUserId(@Param("userId") Long userId);
}
