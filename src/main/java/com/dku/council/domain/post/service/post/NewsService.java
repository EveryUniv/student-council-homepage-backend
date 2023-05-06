package com.dku.council.domain.post.service.post;


import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.post.NewsRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.global.auth.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final GenericPostService<News> postService;
    private final NewsRepository repository;

    public Page<SummarizedGenericPostDto> list(String keyword, List<Long> tagIds, Pageable pageable, int bodySize) {
        Specification<News> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withTags(tagIds));
        return postService.list(repository, spec, pageable, bodySize);
    }

    public Long create(Long userId, RequestCreateNewsDto request) {
        return postService.create(repository, userId, request);
    }

    public ResponseSingleGenericPostDto findOneForGuest(Long id, String address) {
        return postService.findOne(repository, id, null, UserRole.GUEST, address);
    }

    public ResponseSingleGenericPostDto findOne(Long id, Long userId, UserRole role, String address) {
        return postService.findOne(repository, id, userId, role, address);
    }

    public void delete(Long id, Long userId, boolean admin) {
        postService.delete(repository, id, userId, admin);
    }
}
