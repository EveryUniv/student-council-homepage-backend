package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedNewsDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleNewsDto;
import com.dku.council.domain.post.service.NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "총학소식", description = "총학소식 게시판 관련 api")
@RestController
@RequestMapping("/post/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // TODO summary와 description 둘 다 javadoc으로 표현할 수 있게 하기

    /**
     * 게시글 목록으로 조회
     *
     * @param query    제목이나 내용에 포함된 검색어. 지정하지않으면 모든 게시글 조회.
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 총학 소식 목록
     */
    @GetMapping
    public ResponsePage<SummarizedNewsDto> list(@RequestParam(required = false) String query, Pageable pageable) {
        Page<SummarizedNewsDto> list = newsService.list(query, pageable);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @param request 요청 dto
     */
    @PostMapping
    public ResponsePostIdDto create(Authentication auth, @Valid @RequestBody RequestCreateNewsDto request) {
        Long userId = (Long) auth.getPrincipal();
        Long postId = newsService.create(userId, request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 총학소식 게시글 정보
     */
    @GetMapping("/{id}")
    public ResponseSingleNewsDto findOne(@PathVariable Long id, HttpServletRequest request) {
        return newsService.findOne(id, request.getRemoteAddr());
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    public ResponsePostIdDto delete(@PathVariable Long id) {
        newsService.delete(id);
        return new ResponsePostIdDto(id);
    }
}
