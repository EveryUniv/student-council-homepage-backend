package com.dku.council.domain.post.dto;

import com.dku.council.domain.comment.entity.Comment;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentDto {
    private final String major;
    private final String text;
    private final String createdDate;

    public CommentDto(Comment comment) {
        this.major = comment.getUser().getMajor().getDepartment().toString();
        this.createdDate = comment.getCreatedDateText();
        this.text = comment.getText();
    }

    public static List<CommentDto> listOf(List<Comment> entities) {
        return entities.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }
}
