package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.post.model.dto.CommentDto;
import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.News;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseSingleNewsDto {

    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final LocalDateTime createdAt;
    private final List<PostFileDto> files;
    private final List<CommentDto> commentList;


    public ResponseSingleNewsDto(MessageSource messageSource, String baseFileUrl, News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.body = news.getBody();
        this.author = news.getUser().getName();
        this.createdAt = news.getCreatedAt();
        this.files = PostFileDto.listOf(baseFileUrl, news.getFiles());
        this.commentList = CommentDto.listOf(messageSource, news.getComments());
    }
}
