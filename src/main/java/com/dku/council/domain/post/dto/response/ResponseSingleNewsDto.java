package com.dku.council.domain.post.dto.response;

import com.dku.council.domain.post.dto.PostFileDto;
import com.dku.council.domain.post.entity.posttype.News;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseSingleNewsDto {

    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final LocalDateTime createdAt;
    private final List<PostFileDto> files = new ArrayList<>();


    private ResponseSingleNewsDto(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.body = news.getBody();
        this.author = news.getUser().getName();
        this.createdAt = news.getCreatedAt();
    }

    public static ResponseSingleNewsDto fromEntity(News news) {
        return new ResponseSingleNewsDto(news);
    }
}
