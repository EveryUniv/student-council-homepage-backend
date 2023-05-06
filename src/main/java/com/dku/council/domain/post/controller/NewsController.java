package com.dku.council.domain.post.controller;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.service.post.NewsService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.auth.role.GuestAuth;
import com.dku.council.global.auth.role.UserAuth;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.global.util.RemoteAddressUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.dku.council.domain.like.model.LikeTarget.POST;

@Tag(name = "총학소식", description = "총학소식 게시판 관련 api")
@RestController
@RequestMapping("/post/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService postService;
    private final LikeService likeService;

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
        Page<SummarizedGenericPostDto> list = postService.list(keyword, tagIds, pageable, bodySize);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AdminAuth
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
    @GuestAuth
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        String address = RemoteAddressUtil.getProxyableAddr(request);
        if (auth == null) {
            return postService.findOneForGuest(id, address);
        }
        return postService.findOne(id, auth.getUserId(), UserRole.from(auth), address);
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @AdminAuth
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        postService.delete(id, auth.getUserId(), auth.isAdmin());
    }

    /**
     * 게시글에 좋아요 표시
     * 중복으로 좋아요 표시해도 1개만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @PostMapping("/like/{id}")
    @UserAuth
    public void like(AppAuthentication auth, @PathVariable Long id) {
        likeService.like(id, auth.getUserId(), POST);
    }

    /**
     * 좋아요 취소
     * 중복으로 좋아요 취소해도 최초 1건만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @DeleteMapping("/like/{id}")
    @UserAuth
    public void cancelLike(AppAuthentication auth, @PathVariable Long id) {
        likeService.cancelLike(id, auth.getUserId(), LikeTarget.POST);
    }
}
