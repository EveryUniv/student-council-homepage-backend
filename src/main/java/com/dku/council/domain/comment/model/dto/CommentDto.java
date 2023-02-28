package com.dku.council.domain.comment.model.dto;

import com.dku.council.domain.comment.model.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentDto {
    @Schema(name = "댓글 아이디", example = "2")
    private final Long id;

    @Schema(name = "작성자 소속대학", example = "공과대학")
    private final String major;

    @Schema(name = "댓글 본문", example = "이것은 댓글입니다.")
    private final String text;

    @Schema(name = "생성날짜", example = "2023-01-01")
    private final String createdDate;

    public CommentDto(MessageSource messageSource, Comment comment) {
        this.id = comment.getId();
        this.major = comment.getUser().getMajor().getDepartmentName(messageSource);
        this.createdDate = comment.getCreatedDateText();
        this.text = comment.getText();
    }

    public static List<CommentDto> listOf(MessageSource messageSource, List<Comment> entities) {
        return entities.stream()
                .map(ent -> new CommentDto(messageSource, ent))
                .collect(Collectors.toList());
    }
}
