package com.dku.council.domain.comment.controller;

import com.dku.council.domain.comment.model.dto.request.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.config.SecurityConfig;
import com.dku.council.global.config.SwaggerConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "댓글 생성", description = "게시판 관련 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/comment")
public class CommentController {
    private final CommentService commentService;

    // TODO 댓글이 활성화 되어 있는 게시글만 생성
    // TODO 권한 annotation은 통합할 수 있으면 좋겠다.

    /***
     * 게시글에 댓글 생성
     * @param postId      댓글 생성할 게시글 id
     * @param commentDto  댓글 내용(text)
     */
    @PostMapping("/{postId}")
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
    @Secured(SecurityConfig.USER_ROLE)
    public void create(AppAuthentication auth, @PathVariable Long postId, @Valid RequestCreateCommentDto commentDto) {
        commentService.add(postId, auth.getUserId(), commentDto.getText());
    }

    /**
     * 게시글 댓글 수정
     *
     * @param id         댓글 id
     * @param commentDto 수정할 댓글 내용(text)
     */
    @PatchMapping("/{id}")
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
    @Secured(SecurityConfig.USER_ROLE)
    public void edit(AppAuthentication auth, @PathVariable Long id, @Valid RequestCreateCommentDto commentDto) {
        commentService.edit(id, auth.getUserId(), commentDto.getText());
    }

    /**
     * 게시글 댓글 삭제
     *
     * @param id 댓글 id
     */
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = SwaggerConfig.AUTHENTICATION)
    @Secured(SecurityConfig.USER_ROLE)
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        commentService.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
