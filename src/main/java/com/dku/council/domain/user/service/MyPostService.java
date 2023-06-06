package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPostService {

    private final PostRepository postRepository;
    private final ObjectUploadContext uploadContext;
    private final LikeService likeService;
    private final CommentRepository commentRepository;


    @Transactional
    public Page<SummarizedGenericPostDto> listMyLikedPosts(Long userId, Pageable pageable, int bodySize) {
        Page<Long> likedPosts = likeService.getLikedElementIds(userId, pageable, LikeTarget.POST);
        return postRepository.findPageById(likedPosts.getContent(), pageable)
                .map(post -> mapToListDto(post, bodySize));
    }

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyCommentedPosts(Long userId, Pageable pageable, int bodySize) {
        return commentRepository.findAllCommentedByUserId(userId, pageable)
                .map(post -> mapToListDto(post, bodySize));
    }

    private SummarizedGenericPostDto mapToListDto(Post post, int bodySize) {
        return new SummarizedGenericPostDto(uploadContext, bodySize,
                likeService.getCountOfLikes(post.getId(), LikeTarget.POST), post);
    }
}
