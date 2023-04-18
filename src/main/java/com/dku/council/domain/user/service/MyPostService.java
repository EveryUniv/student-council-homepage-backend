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
    private final ObjectUploadContext uploadContext;
    private final LikeService likeService;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyPosts(Long userId, Pageable pageable, int bodySize) {
        return postRepository.findAllByUserId(userId, pageable)
                .map(post -> mapToListDto(post, bodySize));
    }

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyLikedPosts(Long userId, Pageable pageable, int bodySize) {
        Page<Long> likedPosts = likeService.getLikedElementIds(userId, pageable, LikeTarget.POST);
        List<SummarizedGenericPostDto> posts = postRepository.findAllById(likedPosts.getContent()).stream()
                .map(post -> mapToListDto(post, bodySize))
                .collect(Collectors.toList());
        return new PageImpl<>(posts, pageable, likedPosts.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyCommentedPosts(Long userId, Pageable pageable, int bodySize) {
        return commentRepository.findAllCommentByUserId(userId, pageable)
                .map(post -> mapToListDto(post, bodySize));
    }

    private SummarizedGenericPostDto mapToListDto(Post post, int bodySize) {
        return new SummarizedGenericPostDto(uploadContext, bodySize,
                likeService.getCountOfLikes(post.getId(), LikeTarget.POST), post);
    }
}
