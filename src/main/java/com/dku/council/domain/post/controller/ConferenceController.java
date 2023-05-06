package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.list.SummarizedConferenceDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateConferenceDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.service.post.ConferenceService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.model.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "회의록 게시판", description = "회의록 게시판 관련 api")
@RestController
@RequestMapping("/post/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceService service;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @param pageable 페이징 size, sort, page
     * @return 페이징 된 회의록 목록
     */
    @GetMapping
    public ResponsePage<SummarizedConferenceDto> list(@RequestParam(required = false) String keyword,
                                                      @RequestParam(defaultValue = "50") int bodySize,
                                                      @ParameterObject Pageable pageable) {
        Page<SummarizedConferenceDto> list = service.list(keyword, pageable, bodySize);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록 (Admin)
     *
     * @return 생성된 게시글 id
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AdminAuth
    public ResponseIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreateConferenceDto request) {
        Long postId = service.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 삭제 (Admin)
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @AdminAuth
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        service.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
