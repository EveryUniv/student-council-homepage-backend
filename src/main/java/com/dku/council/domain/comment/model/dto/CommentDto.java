package com.dku.council.domain.comment.model.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CommentDto {
    @Schema(name = "댓글 아이디", example = "2")
    private final Long id;

    @Schema(name = "작성자 소속대학", example = "공과대학")
    private final String major;

    @Schema(name = "댓글 본문", example = "이것은 댓글입니다.")
    private final String text;

    @Schema(name = "생성날짜", example = "2023-01-01")
    private final LocalDate createdAt;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.major = comment.getUser().getMajor().getDepartment();
        this.createdAt = comment.getCreatedAt().toLocalDate();
        this.text = comment.getText();
    }
}
