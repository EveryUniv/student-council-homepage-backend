package com.dku.council.domain.comment.controller;

import com.dku.council.domain.comment.model.dto.request.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "댓글 생성", description = "게시판 관련 댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/comment")
public class CommentController {
    private final CommentService commentService;

    // TODO 댓글이 활성화 되어 있는 게시글만 생성

    /***
     * 게시글에 댓글 생성
     * @param postId      댓글 생성할 게시글 id
     * @param commentDto  댓글 내용(text)
     */
    @PostMapping("/{postId}")
    public void create(Authentication authentication, @PathVariable Long postId, @Valid RequestCreateCommentDto commentDto){
        Long userId = (Long) authentication.getPrincipal();
        commentService.add(postId, userId, commentDto.getText());
    }

    /**
     * 게시글 댓글 수정
     * @param id          댓글 id
     * @param commentDto  수정할 댓글 내용(text)
     */
    @PatchMapping("/{id}")
    public void edit(Authentication authentication, @PathVariable Long id, @Valid RequestCreateCommentDto commentDto){
        Long userId = (Long) authentication.getPrincipal();
        commentService.edit(id, userId, commentDto.getText());
    }

    /**
     * 게시글 댓글 삭제
     * @param id  댓글 id
     */
    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id){
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        commentService.delete(id, userId, isAdmin);
    }
}
