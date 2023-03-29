package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.CommentRepository;
import com.dku.council.domain.comment.model.dto.CommentedPostResponseDto;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// TODO Test it
@Service
@RequiredArgsConstructor
public class MyPostService {

    private final PostRepository postRepository;
    private final FileUploadService fileUploadService;
    private final LikeService likeService;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyPosts(Long userId, Pageable pageable, int bodySize) {
        Page<Post> list = postRepository.findAllByUserId(userId, pageable);

        return list.map(
                e -> new SummarizedGenericPostDto(fileUploadService.getBaseURL(),
                        bodySize,
                        likeService.getCountOfLikes(e.getId(), LikeTarget.POST), e)
        );
    }

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyLikedPosts(Long userId, Pageable pageable, int bodySize) {
        Page<Long> likedPosts = likeService.getLikedElementIds(userId, pageable, LikeTarget.POST);
        List<SummarizedGenericPostDto> posts = postRepository.findAllById(likedPosts.getContent()).stream()
                .map(post -> new SummarizedGenericPostDto(fileUploadService.getBaseURL(),
                        bodySize,
                        likeService.getCountOfLikes(post.getId(), LikeTarget.POST), post))
                .collect(Collectors.toList());
        return new PageImpl<>(posts, pageable, likedPosts.getTotalElements());
    }

    // TODO Comment별 post가 중복되지 않게 가져와야 함.
    @Transactional(readOnly = true)
    public Page<CommentedPostResponseDto> listMyCommentedPosts(Long userId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByUserId(userId, pageable);
        return comments.map(comment -> {
            Post post = comment.getPost();
            return new CommentedPostResponseDto(post, comment);
        });
    }
}
