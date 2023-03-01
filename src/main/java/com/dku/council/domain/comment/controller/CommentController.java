package com.dku.council.domain.comment.controller;

import com.dku.council.domain.comment.model.dto.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "댓글 생성", description = "게시판 관련 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/comment")
public class CommentController {
    private final CommentService commentService;

    /**
     * 게시글에 댓글 생성
     *
     * @param postId      댓글 생성할 게시글 id
     * @param commentDto  댓글 내용(text)
     */
    @PostMapping("/{postId}")
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth,
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
    @PatchMapping("/{id}")
    @UserOnly
    public ResponseIdDto edit(AppAuthentication auth,
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
    @DeleteMapping("/{id}")
    @UserOnly
    public ResponseIdDto delete(AppAuthentication auth, @PathVariable Long id) {
        Long deleteId = commentService.delete(id, auth.getUserId(), auth.isAdmin());
        return new ResponseIdDto(deleteId);
    }
}
