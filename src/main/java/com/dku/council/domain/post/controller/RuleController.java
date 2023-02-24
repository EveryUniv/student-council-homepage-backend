package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedRuleDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateRuleDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleRuleDto;
import com.dku.council.domain.post.service.RuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "총학 회칙", description = "총학 회칙 관련 api")
@RestController
@RequestMapping("/post/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     * 게시글 목록으로 조회
     * @param query     제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param pageable  페이징 size, sort, page
     * @return          페지이된 총학 회칙 목록
     */
    @GetMapping
    public ResponsePage<SummarizedRuleDto> list(@RequestParam(required = false) String query, Pageable pageable) {
        Page<SummarizedRuleDto> list = ruleService.list(query, pageable);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     * @param request 요청 dto
     * @return 게시글 id
     */
    @PostMapping
    public ResponsePostIdDto create(Authentication auth, @Valid @RequestBody RequestCreateRuleDto request){
        Long userId = (Long) auth.getPrincipal();
        Long postId = ruleService.create(userId, request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     * @param id 조회할 게시글 id
     * @return 총학 회칙 게시글 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleRuleDto findOne(@PathVariable Long id, HttpServletRequest request) {
        return ruleService.findOne(id, request.getRemoteAddr());
    }

    /**
     * 게시글 삭제
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        ruleService.delete(id);
    }

}
