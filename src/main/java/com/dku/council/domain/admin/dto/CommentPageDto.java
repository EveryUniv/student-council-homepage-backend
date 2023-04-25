package com.dku.council.domain.admin.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentPageDto {
    private final Long id;
    private final Long postId;
    private final String postTitle;
    private final String text;
    private final String status;

    public CommentPageDto(Comment comment){
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.postTitle = comment.getPost().getTitle();
        this.text = comment.getText();
        this.status = comment.getStatus().toString();
    }
}
