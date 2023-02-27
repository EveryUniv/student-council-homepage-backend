package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedGeneralForumDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.infra.nhn.service.FileUploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "자유게시판", description = "자유게시판 관련 api")
@RestController
@RequestMapping("/post/general-forum")
@RequiredArgsConstructor
public class GeneralForumController {

    private final GenericPostService<GeneralForum> generalForumService;
    private final FileUploadService fileUploadService;

    /**
     * 게시글 목록 및 카테고리 조회
     *
     * @param keyword    제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param categoryId 탐색할 카테고리 ID. 지정하지않으면 모든 게시글 조회.
     * @param pageable   페이징 size, sort, page
     * @return 페이징된 자유게시판 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGeneralForumDto> list(@RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) Long categoryId,
                                                        @ParameterObject Pageable pageable) {
        Specification<GeneralForum> spec = PostSpec.genericPostCondition(keyword, categoryId);
        Page<SummarizedGeneralForumDto> list = generalForumService.list(spec, pageable)
                .map(post -> new SummarizedGeneralForumDto(fileUploadService.getBaseURL(), post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @param request 요청 dto
     */
    @PostMapping
    public ResponsePostIdDto create(AppAuthentication auth, @Valid @RequestBody RequestCreateGeneralForumDto request) {
        Long postId = generalForumService.create(auth.getUserId(), request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 자유게시판 게시글 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        return generalForumService.findOne(id, auth.getUserId(), request.getRemoteAddr());
    }

    /**
     * 게시글 삭제 For Admin & Owner
     *
     * @param auth 사용자 인증정보
     * @param id   삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        generalForumService.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
