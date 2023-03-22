package com.dku.council.domain.comment.model.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CommentDto {
    @Schema(description = "댓글 아이디", example = "2")
    private final Long id;

    @Schema(description = "작성자", example = "익명")
    private final String author;

    @Schema(description = "댓글 본문", example = "이것은 댓글입니다.")
    private final String text;

    @Schema(description = "내가 쓴 게시물인지?", example = "true")
    private final boolean isMine;

    @Schema(description = "생성날짜", example = "2023-01-01")
    private final LocalDate createdAt;

    public CommentDto(Comment comment, String author, boolean isMine) {
        this.id = comment.getId();
        this.author = author;
        this.createdAt = comment.getCreatedAt().toLocalDate();
        this.isMine = isMine;
        this.text = comment.getText();
    }
}
