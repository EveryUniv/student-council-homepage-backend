package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.CommentRepository;
import com.dku.council.domain.comment.model.dto.CommentedPostResponseDto;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MyCommentedPostService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<CommentedPostResponseDto> listMyCommentedPosts(Long userId, Pageable pageable){
        Page<Comment> comments = commentRepository.findAllByUserId(userId, pageable);
        return comments.map(comment -> {
            Post post = comment.getPost();
            return new CommentedPostResponseDto(post, comment);
        });
    }
}
