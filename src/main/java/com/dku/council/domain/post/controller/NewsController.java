package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "총학소식", description = "총학소식 게시판 관련 api")
@RestController
@RequestMapping("/post/news")
@RequiredArgsConstructor
public class NewsController {

    private final GenericPostService<News> postService;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지않으면 모든 게시글 조회.
     * @param tagIds   조회할 태그 목록. or 조건으로 검색된다. 지정하지않으면 모든 게시글 조회.
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @return 페이징된 총학 소식 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGenericPostDto> list(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) List<Long> tagIds,
                                                       @RequestParam(defaultValue = "50") int bodySize,
                                                       @ParameterObject Pageable pageable) {
        Specification<News> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withTags(tagIds));
        Page<SummarizedGenericPostDto> list = postService.list(spec, pageable)
                .map(post -> new SummarizedGenericPostDto(postService.getFileBaseUrl(), bodySize, post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreateNewsDto request) {
        Long postId = postService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 총학소식 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        return postService.findOne(id, auth.getUserId(), request.getRemoteAddr());
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @UserOnly
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        postService.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
