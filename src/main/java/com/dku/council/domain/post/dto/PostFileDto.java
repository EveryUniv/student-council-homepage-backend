package com.dku.council.domain.post.dto;

import com.dku.council.domain.post.entity.PostFile;
import lombok.Getter;

@Getter
public class PostFileDto {
    private final Long id;
    private final String url;
    private final String originalName;

    private PostFileDto(String baseUrl, PostFile file) {
        this.id = file.getId();
        this.url = baseUrl + file.getFileUrl();
        this.originalName = file.getFileName();
    }

    public static PostFileDto fromEntity(String baseUrl, PostFile file) {
        return new PostFileDto(baseUrl, file);
    }
}
