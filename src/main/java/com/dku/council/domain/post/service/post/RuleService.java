package com.dku.council.domain.post.service.post;


import com.dku.council.domain.post.model.dto.list.SummarizedRuleDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateRuleDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.post.RuleRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final GenericPostService<Rule> postService;
    private final RuleRepository repository;


    public Page<SummarizedRuleDto> list(String keyword, Pageable pageable, int bodySize) {
        Specification<Rule> spec = PostSpec.withTitleOrBody(keyword);
        return postService.list(repository, spec, pageable, bodySize, SummarizedRuleDto::new);
    }

    public Long create(Long userId, RequestCreateRuleDto request) {
        return postService.create(repository, userId, request);
    }

    public ResponseSingleGenericPostDto findOne(Long id, Long userId, String address) {
        return postService.findOne(repository, id, userId, address);
    }

    public void delete(Long id, Long userId, boolean admin) {
        postService.delete(repository, id, userId, admin);
    }
}
