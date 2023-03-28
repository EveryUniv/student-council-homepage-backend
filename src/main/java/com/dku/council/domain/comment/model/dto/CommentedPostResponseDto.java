package com.dku.council.domain.comment.model.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CommentedPostResponseDto {
    @Schema(description = "게시글 아이디", example = "1")
    private final Long id;

    @Schema(description = "본문 제목", example = "제목")
    private final String title;

    @Schema(description = "본문", example = "내용")
    private final String body;

    @Schema(description = "댓글 아이디", example = "익명")
    private final Long commentId;

    @Schema(description = "댓글 내용", example = "댓글")
    private final String text;

    @Schema(description = "생성 날짜", example = "2023-01-01")
    private final LocalDate createdAt;

    public CommentedPostResponseDto(Post post, Comment comment) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.commentId = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt().toLocalDate();
    }
}
