package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedConferenceDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateConferenceDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.infra.nhn.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "회의록 게시판", description = "회의록 게시판 관련 api")
@RestController
@RequestMapping("/post/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final GenericPostService<Conference> conferenceService;
    private final FileUploadService fileUploadService;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param pageable 페이징 size, sort, page
     * @return 페이징 된 회의록 목록
     */
    @GetMapping
    public ResponsePage<SummarizedConferenceDto> list(@RequestParam(required = false) String keyword,
                                                      @ParameterObject Pageable pageable) {
        Specification<Conference> spec = PostSpec.genericPostCondition(keyword, null);
        Page<SummarizedConferenceDto> list = conferenceService.list(spec, pageable)
                .map(post -> new SummarizedConferenceDto(fileUploadService.getBaseURL(), post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @param request 요청 dto
     * @return 생성된 게시글 id
     */
    @PostMapping
    public ResponsePostIdDto create(Authentication auth, @Valid @RequestBody RequestCreateConferenceDto request) {
        Long userId = (Long) auth.getPrincipal();
        Long postId = conferenceService.create(userId, request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        conferenceService.delete(id, userId, isAdmin);
    }
}
