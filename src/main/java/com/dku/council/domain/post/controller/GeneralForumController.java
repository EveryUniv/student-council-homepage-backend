package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedGeneralForumDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGeneralForumDto;
import com.dku.council.domain.post.service.GeneralForumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "자유게시판", description = "자유게시판 관련 api")
@RestController
@RequestMapping("/post/general-forum")
@RequiredArgsConstructor
public class GeneralForumController {

    private final GeneralForumService generalForumService;

    /**
     * 게시글 목록 및 카테고리 조회
     * @param query    제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param category 카테고리에 해당하는 모든 게시물 조회.
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 자유게시판 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGeneralForumDto> list(@RequestParam(required = false) String query,
                                                        @RequestParam(required = false) String category,
                                                        Pageable pageable) {
        Page<SummarizedGeneralForumDto> list = generalForumService.list(query, category, pageable);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @param request 요청 dto
     */
    @PostMapping
    public ResponsePostIdDto create(Authentication auth, @Valid @RequestBody RequestCreateGeneralForumDto request) {
        Long userId = (Long) auth.getPrincipal();
        Long postId = generalForumService.create(userId, request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 자유게시판 게시글 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleGeneralForumDto findOne(Authentication auth, @PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return generalForumService.findOne(id, request.getRemoteAddr(), userId);
    }

    /**
     * 게시글 삭제 For Admin & Owner
     * @param auth 사용자 인증정보
     * @param id   삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        generalForumService.delete(id, userId, isAdmin);
    }
}
