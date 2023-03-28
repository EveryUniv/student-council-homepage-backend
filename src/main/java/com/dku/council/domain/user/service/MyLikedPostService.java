package com.dku.council.domain.user.service;

import com.dku.council.domain.like.model.entity.PostLike;
import com.dku.council.domain.like.repository.PostLikePersistenceRepository;
import com.dku.council.domain.like.service.PostLikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyLikedPostService {

    private final PostLikePersistenceRepository postLikeRepository;
    private final FileUploadService fileUploadService;
    private final PostLikeService postLikeService;

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyLikedPosts(Long userId, Pageable pageable, int bodySize){
        Page<PostLike> list = postLikeRepository.findAllByUserId(userId, pageable);

        return list.map(
                e -> new SummarizedGenericPostDto(fileUploadService.getBaseURL(),
                        bodySize,
                        postLikeService.getCountOfLikes(e.getPost().getId()), e.getPost())
        );
    }
}
