package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseSingleRuleDto {
    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final LocalDateTime createdAt;
    private final List<PostFileDto> files;
    private final List<CommentDto> commentList;


    public ResponseSingleRuleDto(String baseFileUrl, Rule rule) {
        this.id = rule.getId();
        this.title = rule.getTitle();
        this.body = rule.getBody();
        this.author = rule.getUser().getName();
        this.createdAt = rule.getCreatedAt();
        this.files = PostFileDto.listOf(baseFileUrl, rule.getFiles());
        this.commentList = CommentDto.listOf(rule.getComments());
    }
}
