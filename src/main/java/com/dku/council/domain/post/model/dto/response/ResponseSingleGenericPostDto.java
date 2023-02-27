package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.Post;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
public class ResponseSingleGenericPostDto {

    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final String category;
    private final LocalDateTime createdAt;
    private final List<PostFileDto> files;
    private final List<CommentDto> commentList;
    private final boolean isMine;

    public ResponseSingleGenericPostDto(MessageSource messageSource, String baseFileUrl, Long userId, Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.author = post.getUser().getName();
        this.category = Optional.ofNullable(post.getCategory())
                .map(Category::getName)
                .orElse(null);
        this.createdAt = post.getCreatedAt();
        this.files = PostFileDto.listOf(baseFileUrl, post.getFiles());
        this.commentList = CommentDto.listOf(messageSource, post.getComments());
        this.isMine = post.getUser().getId().equals(userId);
    }
}
