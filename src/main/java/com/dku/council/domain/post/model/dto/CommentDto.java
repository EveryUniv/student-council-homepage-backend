package com.dku.council.domain.post.model.dto;

import com.dku.council.domain.comment.entity.Comment;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentDto {
    private final String major;
    private final String text;
    private final String createdDate;

    public CommentDto(MessageSource messageSource, Comment comment) {
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
