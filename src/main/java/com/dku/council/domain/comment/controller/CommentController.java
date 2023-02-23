package com.dku.council.domain.comment.controller;

import com.dku.council.domain.comment.model.dto.request.RequestCreateCommentDto;
import com.dku.council.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public void create(Authentication authentication, @PathVariable Long postId, @Valid RequestCreateCommentDto commentDto){
        Long userId = (Long) authentication.getPrincipal();
        commentService.add(postId, userId, commentDto.getText());
    }

    @PatchMapping("/{id}")
    public void edit(Authentication authentication, @PathVariable Long id, @Valid RequestCreateCommentDto commentDto){
        Long userId = (Long) authentication.getPrincipal();
        commentService.edit(id, userId, commentDto.getText());
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id){
        Long userId = (Long) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        commentService.delete(id, userId, isAdmin);
    }
}
