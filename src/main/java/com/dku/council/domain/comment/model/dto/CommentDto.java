package com.dku.council.domain.comment.model.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    @Schema(description = "댓글 아이디", example = "2")
    private final Long id;

    @Schema(description = "작성자", example = "익명")
    private final String author;

    @Schema(description = "댓글 본문", example = "이것은 댓글입니다.")
    private final String text;

    @Schema(description = "좋아요 수", example = "16")
    private final int likes;

    @Schema(description = "내가 좋아요를 눌렀는지?", example = "false")
    private final boolean isLiked;

    @Schema(description = "내가 쓴 게시물인지?", example = "true")
    private final boolean isMine;

    @Schema(description = "생성날짜")
    private final LocalDateTime createdAt;

    public CommentDto(Comment comment, String author, int likes, boolean isMine, boolean isLiked) {
        this.id = comment.getId();
        this.author = author;
        this.createdAt = comment.getCreatedAt();
        this.isMine = isMine;
        this.likes = likes;
        this.isLiked = isLiked;
        this.text = comment.getText();
    }
}
