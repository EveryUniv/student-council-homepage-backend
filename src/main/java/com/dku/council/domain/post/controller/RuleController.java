package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.list.SummarizedRuleDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateRuleDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "회칙", description = "회칙 관련 api")
@RestController
@RequestMapping("/post/rule")
@RequiredArgsConstructor
public class RuleController {

    private final GenericPostService<Rule> postService;
    private final MessageSource messageSource;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword 제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @return 페이징된 회칙 목록
     */
    @GetMapping
    public ResponsePage<SummarizedRuleDto> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(defaultValue = "50") int bodySize,
                                                @ParameterObject Pageable pageable) {
        Specification<Rule> spec = PostSpec.withTitleOrBody(keyword);
        Page<SummarizedRuleDto> list = postService.list(spec, pageable)
                .map(post -> new SummarizedRuleDto(postService.getFileBaseUrl(), bodySize, post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록 (Admin)
     *
     * @return 게시글 id
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AdminOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreateRuleDto request) {
        Long postId = postService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 회칙 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        return postService.findOne(id, auth.getUserId(), request.getRemoteAddr());
    }

    /**
     * 게시글 삭제 (Admin)
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @AdminOnly
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        postService.delete(id, auth.getUserId(), auth.isAdmin());
    }

}
