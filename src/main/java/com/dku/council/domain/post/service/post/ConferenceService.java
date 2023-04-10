package com.dku.council.domain.post.service.post;


import com.dku.council.domain.post.model.dto.list.SummarizedConferenceDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateConferenceDto;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.post.repository.post.ConferenceRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConferenceService {
    private final GenericPostService<Conference> postService;
    private final ConferenceRepository repository;


    public Page<SummarizedConferenceDto> list(String keyword, Pageable pageable, int bodySize) {
        Specification<Conference> spec = PostSpec.withTitleOrBody(keyword);
        return postService.list(repository, spec, pageable, bodySize, SummarizedConferenceDto::new);
    }

    public Long create(Long userId, RequestCreateConferenceDto request) {
        return postService.create(repository, userId, request);
    }

    public void delete(Long id, Long userId, boolean admin) {
        postService.delete(repository, id, userId, admin);
    }
}
