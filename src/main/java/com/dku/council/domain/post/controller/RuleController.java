package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedRuleDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateRuleDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.RuleService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.infra.nhn.service.FileUploadService;
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

// TODO Test it
@Tag(name = "총학 회칙", description = "총학 회칙 관련 api")
@RestController
@RequestMapping("/post/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;
    private final FileUploadService fileUploadService;
    private final MessageSource messageSource;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword 제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @return 페이징된 총학 회칙 목록
     */
    @GetMapping
    public ResponsePage<SummarizedRuleDto> list(@RequestParam(required = false) String keyword,
                                                @ParameterObject Pageable pageable) {
        Specification<Rule> spec = PostSpec.genericPostCondition(keyword, null);
        Page<SummarizedRuleDto> list = ruleService.list(spec, pageable)
                .map(post -> new SummarizedRuleDto(messageSource, fileUploadService.getBaseURL(), post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록 (Admin)
     *
     * @return 게시글 id
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AdminOnly
    public ResponsePostIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreateRuleDto request) {
        Long postId = ruleService.create(auth.getUserId(), request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 총학 회칙 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        return ruleService.findOne(id, auth.getUserId(), request.getRemoteAddr());
    }

    /**
     * 게시글 삭제 (Admin)
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @AdminOnly
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        ruleService.delete(id, auth.getUserId(), auth.isAdmin());
    }

}
