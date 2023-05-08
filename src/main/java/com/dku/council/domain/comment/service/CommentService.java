package com.dku.council.domain.comment.service;

import com.dku.council.domain.comment.exception.CommentNotFoundException;
import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.comment.model.entity.CommentLog;
import com.dku.council.domain.comment.repository.CommentLogRepository;
import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;
    private final CommentRepository commentRepository;
    private final CommentLogRepository commentLogRepository;


    /**
     * 댓글 목록을 가져옵니다. 작성자는 기본적으로 익명으로 채워집니다.
     *
     * @param postId   게시글 ID
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    public Page<CommentDto> list(Long postId, Long userId, Pageable pageable) {
        return list(postId, userId, pageable, null);
    }


    /**
     * 댓글 목록을 가져옵니다. 작성자 매핑 함수를 통해 댓글 작성자를 매핑합니다.
     * 매핑 함수에 null을 전달하면 익명으로 채워집니다.
     *
     * @param postId   게시글 ID
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @param mapper   dto 매핑 함수
     * @return 페이징된 댓글 목록
     */
    public Page<CommentDto> list(Long postId, Long userId, Pageable pageable, CommentMapper mapper) {
        postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return commentRepository.findAllByPostId(postId, pageable)
                .map(e -> {
                    CommentDto dto = new CommentDto(e, User.ANONYMITY,
                            likeService.getCountOfLikes(e.getId(), LikeTarget.COMMENT),
                            e.getUser().getId().equals(userId),
                            likeService.isLiked(e.getId(), userId, LikeTarget.COMMENT));
                    return mapper == null ? dto : mapper.map(e, dto);
                });
    }

    /**
     * Post에 댓글을 추가합니다.
     *
     * @param postId  게시글 ID
     * @param userId  사용자 ID
     * @param content 댓글 내용
     */
    public Long create(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Comment comment = Comment.builder()
                .user(user)
                .text(content)
                .build();

        comment.changePost(post);
        comment = commentRepository.save(comment);
        return comment.getId();
    }

    /**
     * 댓글을 수정합니다. 사용자가 댓글을 수정할 수 있는 권한이 있어야합니다.
     *
     * @param commentId 댓글 ID
     * @param userId    사용자 ID
     * @param content   댓글 내용
     */
    public Long edit(Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
        Post post = comment.getPost();
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!comment.getUser().getId().equals(userId)) {
            throw new NotGrantedException();
        }

        CommentLog commentLog = CommentLog.builder()
                .post(post)
                .user(user)
                .text(comment.getText())
                .build();

        commentLogRepository.save(commentLog);

        comment.updateText(content);
        return commentId;
    }

    /**
     * 댓글을 삭제합니다. 사용자가 댓글을 삭제할 수 있는 권한이 있어야 합니다. Admin 은 즉시 삭제 가능.
     *
     * @param commentId 댓글 ID
     * @param userId    사용자 ID
     * @param isAdmin   Admin 여부
     */
    public Long delete(Long commentId, Long userId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        if (isAdmin) {
            comment.updateStatus(CommentStatus.DELETED_BY_ADMIN);
        } else if (comment.getUser().getId().equals(userId)) {
            comment.updateStatus(CommentStatus.DELETED);
        } else {
            throw new NotGrantedException();
        }

        return commentId;
    }

    @FunctionalInterface
    public interface CommentMapper {
        CommentDto map(Comment entity, CommentDto parent);
    }
}
