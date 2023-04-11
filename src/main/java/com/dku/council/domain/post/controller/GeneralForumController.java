package com.dku.council.domain.post.controller;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.model.dto.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.GeneralForumCommentDto;
import com.dku.council.domain.post.model.dto.response.ResponseGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GeneralForumService;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.global.util.RemoteAddressUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@Tag(name = "자유게시판", description = "자유게시판 관련 api")
@RestController
@RequestMapping("/post/general-forum")
@RequiredArgsConstructor
public class GeneralForumController {

    private final CommentService commentService;
    private final GenericPostService<GeneralForum> postService;
    private final GeneralForumService forumService;
    private final LikeService likeService;

    /**
     * 게시글 목록 및 태그 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param tagIds   조회할 태그 목록. or 조건으로 검색된다. 지정하지않으면 모든 게시글 조회.
     * @param pageable 페이징 size, sort, page
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @return 페이징된 자유게시판 목록
     */
    @GetMapping
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public ResponsePage<SummarizedGenericPostDto> list(AppAuthentication auth,
                                                       @RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) List<Long> tagIds,
                                                       @RequestParam(defaultValue = "50") int bodySize,
                                                       @ParameterObject Pageable pageable) {
        Specification<GeneralForum> spec = PostSpec.withTags(tagIds);
        spec = spec.and(PostSpec.withTitleOrBody(keyword));
        System.out.println(auth.getUserId());
        Page<SummarizedGenericPostDto> list = postService.list(spec, pageable, bodySize, auth.getUserId());
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth,
                                @Valid @ModelAttribute RequestCreateGeneralForumDto request) {
        Long postId = forumService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 자유게시판 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponseGeneralForumDto findOne(AppAuthentication auth,
                                           @PathVariable Long id,
                                           HttpServletRequest request) {
        return postService.findOne(id, auth.getUserId(),
                RemoteAddressUtil.getProxyableAddr(request),
                ResponseGeneralForumDto::new);
    }

    /**
     * 게시글 삭제
     * 게시글 삭제시 연관된 파일, 댓글, 좋아요 등이 모두 함께 삭제됩니다.
     *
     * @param auth 사용자 인증정보
     * @param id   삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @UserOnly
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        postService.delete(id, auth.getUserId(), auth.isAdmin());
    }

    /**
     * 게시글을 블라인드 처리
     * 블라인드 처리된 게시글은 운영진만 볼 수 있습니다.
     *
     * @param id 블라인드 처리할 게시글 id
     */
    @PatchMapping("/blind/{id}")
    @AdminOnly
    public void blind(@PathVariable Long id) {
        postService.blind(id);
    }

    /**
     * 특정 게시글 블라인드 해제
     * 블라인드 해제는 운영진만 할 수 있습니다.
     *
     * @param id 블라인드 해제할 게시글 id
     */
    @PatchMapping("/unblind/{id}")
    @AdminOnly
    public void unblind(@PathVariable Long id) {
        postService.unblind(id);
    }

    /**
     * 댓글 목록 가져오기
     *
     * @param postId 댓글 생성할 게시글 id
     */
    @GetMapping("/comment/{postId}")
    @UserOnly
    public ResponsePage<CommentDto> listComment(AppAuthentication auth,
                                                @PathVariable Long postId,
                                                @ParameterObject Pageable pageable) {
        Page<CommentDto> comments = commentService.list(postId, auth.getUserId(), pageable,
                (ent, dto) -> {
                    User user = ent.getUser();
                    return new GeneralForumCommentDto(ent, dto, user.getNickname(), user.getMajor().getName());
                });
        return new ResponsePage<>(comments);
    }

    /**
     * 게시글에 댓글 생성
     *
     * @param postId     댓글 생성할 게시글 id
     * @param commentDto 댓글 내용(text)
     */
    @PostMapping("/comment/{postId}")
    @UserOnly
    public ResponseIdDto createComment(AppAuthentication auth,
                                       @PathVariable Long postId,
                                       @Valid @RequestBody RequestCreateCommentDto commentDto) {
        Long id = commentService.create(postId, auth.getUserId(), commentDto.getText());
        return new ResponseIdDto(id);
    }

    /**
     * 게시글 댓글 수정
     * 댓글을 수정할 수 있는 사람은 본인뿐입니다. Admin도 다른 사람의 댓글은 수정할 수 없습니다. (조작 의혹 방지)
     *
     * @param id         댓글 id
     * @param commentDto 수정할 댓글 내용(text)
     */
    @PatchMapping("/comment/{id}")
    @UserOnly
    public ResponseIdDto editComment(AppAuthentication auth,
                                     @PathVariable Long id,
                                     @Valid @RequestBody RequestCreateCommentDto commentDto) {
        Long editId = commentService.edit(id, auth.getUserId(), commentDto.getText());
        return new ResponseIdDto(editId);
    }

    /**
     * 게시글 댓글 삭제
     * 본인이 쓴 댓글이거나 admin인 경우에 삭제할 수 있습니다.
     *
     * @param id 댓글 id
     */
    @DeleteMapping("/comment/{id}")
    @UserOnly
    public ResponseIdDto deleteComment(AppAuthentication auth, @PathVariable Long id) {
        Long deleteId = commentService.delete(id, auth.getUserId(), auth.isAdmin());
        return new ResponseIdDto(deleteId);
    }

    /**
     * 게시글에 좋아요 표시.
     * 중복으로 좋아요 표시해도 1개만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @PostMapping("/like/{id}")
    @UserOnly
    public void like(AppAuthentication auth, @PathVariable Long id) {
        likeService.like(id, auth.getUserId(), LikeTarget.POST);
    }

    /**
     * 좋아요 취소
     * 중복으로 좋아요 취소해도 최초 1건만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @DeleteMapping("/like/{id}")
    @UserOnly
    public void cancelLike(AppAuthentication auth, @PathVariable Long id) {
        likeService.cancelLike(id, auth.getUserId(), LikeTarget.POST);
    }
}
