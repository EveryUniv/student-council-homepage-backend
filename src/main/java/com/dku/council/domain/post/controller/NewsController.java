package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePostIdDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.config.SwaggerConfig;
import com.dku.council.infra.nhn.service.FileUploadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "총학소식", description = "총학소식 게시판 관련 api")
@RestController
@RequestMapping("/post/news")
@RequiredArgsConstructor
public class NewsController {

    private final GenericPostService<News> postService;
    private final FileUploadService fileUploadService; // TODO 썩 좋은건 아닌데 나중에 리펙토링

    // TODO summary와 description 둘 다 javadoc으로 표현할 수 있게 하기
    // TODO void를 반환하는 api들도 Swagger에서는 모두 SuccessResponseDto를 반환하는 걸로 보이게 하기

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword 제목이나 내용에 포함된 검색어. 지정하지않으면 모든 게시글 조회.
     * @return 페이징된 총학 소식 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGenericPostDto> list(@RequestParam(required = false) String keyword,
                                                       @ParameterObject Pageable pageable) {
        Specification<News> spec = PostSpec.genericPostCondition(keyword, null);
        Page<SummarizedGenericPostDto> list = postService.list(spec, pageable)
                .map(post -> new SummarizedGenericPostDto(fileUploadService.getBaseURL(), post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @param request 요청 dto
     */
    @PostMapping
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
    public ResponsePostIdDto create(AppAuthentication auth, @Valid @RequestBody RequestCreateNewsDto request) {
        Long postId = postService.create(auth.getUserId(), request);
        return new ResponsePostIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 총학소식 게시글 정보
     */
    @GetMapping("/{id}")
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
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
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        postService.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
