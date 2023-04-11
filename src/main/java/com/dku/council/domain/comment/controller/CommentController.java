package com.dku.council.domain.comment.controller;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 부가 기능", description = "댓글 부가 기능 관련 api")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final LikeService likeService;
    
    /**
     * 댓글에 좋아요 표시.
     * 중복으로 좋아요 표시해도 1개만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @PostMapping("/like/{id}")
    @UserAuth
    public void like(AppAuthentication auth, @PathVariable Long id) {
        likeService.like(id, auth.getUserId(), LikeTarget.COMMENT);
    }

    /**
     * 댓글 좋아요 취소
     * 중복으로 좋아요 취소해도 최초 1건만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @DeleteMapping("/like/{id}")
    @UserAuth
    public void cancelLike(AppAuthentication auth, @PathVariable Long id) {
        likeService.cancelLike(id, auth.getUserId(), LikeTarget.COMMENT);
    }
}
