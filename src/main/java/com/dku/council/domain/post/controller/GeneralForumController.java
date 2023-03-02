package com.dku.council.domain.post.controller;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.model.dto.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.domain.post.model.dto.page.SummarizedGeneralForumDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
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

@Tag(name = "자유게시판", description = "자유게시판 관련 api")
@RestController
@RequestMapping("/post/general-forum")
@RequiredArgsConstructor
public class GeneralForumController {

    private final CommentService commentService;
    private final GenericPostService<GeneralForum> postService;

    /**
     * 게시글 목록 및 태그 조회
     *
     * @param keyword    제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param tagId 탐색할 태그 ID. 지정하지않으면 모든 게시글 조회.
     * @param pageable   페이징 size, sort, page
     * @return 페이징된 자유게시판 목록
     */
    @GetMapping
    public ResponsePage<SummarizedGeneralForumDto> list(@RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) Long tagId,
                                                        @ParameterObject Pageable pageable) {
        Specification<GeneralForum> spec = PostSpec.genericPostCondition(keyword, tagId);
        Page<SummarizedGeneralForumDto> list = postService.list(spec, pageable)
                .map(post -> new SummarizedGeneralForumDto(postService.getFileBaseUrl(), post));
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth,
                                @Valid @ModelAttribute RequestCreateGeneralForumDto request) {
        Long postId = postService.create(auth.getUserId(), request);
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
    public ResponseSingleGenericPostDto findOne(AppAuthentication auth,
                                                @PathVariable Long id,
                                                HttpServletRequest request) {
        return postService.findOne(id, auth.getUserId(), request.getRemoteAddr());
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
     * 댓글 목록 가져오기
     *
     * @param postId 댓글 생성할 게시글 id
     */
    @GetMapping("/comment/{postId}")
    @UserOnly
    public ResponsePage<CommentDto> listComment(@PathVariable Long postId, @ParameterObject Pageable pageable) {
        Page<CommentDto> comments = commentService.list(postId, pageable);
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
     * todo 수정시 로그 남도록 하자
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
}
