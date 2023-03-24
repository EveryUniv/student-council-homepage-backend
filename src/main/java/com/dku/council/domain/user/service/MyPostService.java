package com.dku.council.domain.user.service;

import com.dku.council.domain.like.service.PostLikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPostService {

    private final PostRepository postRepository;
    private final FileUploadService fileUploadService;
    private final PostLikeService postLikeService;


    @Transactional(readOnly = true)
    public Page<SummarizedPostDto> listMyPosts(Long userId, Pageable pageable, int bodySize) {
        Page<Post> list = postRepository.findAllByUserId(userId, pageable);

        return list.map(
                e -> new SummarizedPostDto(fileUploadService.getBaseURL(),
                        bodySize,
                        postLikeService.getCountOfLikes(e.getId()), e)
        );
    }
}
