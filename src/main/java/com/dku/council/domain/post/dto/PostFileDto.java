package com.dku.council.domain.post.dto;

import com.dku.council.domain.post.model.entity.PostFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFileDto {

    @Schema(description = "파일 아이디", example = "1")
    private final Long id;

    @Schema(description = "파일 url", example = "http://1.2.3.4/1ddee68d-6afb-48d0-9cb6-04a8d8fea4ae.png")
    private final String url;

    @Schema(description = "원본파일 이름", example = "my_image.png")
    private final String originalName;

    public PostFileDto(String baseUrl, PostFile file) {
        this.id = file.getId();
        this.url = baseUrl + file.getFileUrl();
        this.originalName = file.getFileName();
    }

    public static List<PostFileDto> listOf(String baseFileUrl, List<PostFile> entities) {
        return entities.stream()
                .map(file -> new PostFileDto(baseFileUrl, file))
                .collect(Collectors.toList());
    }
}
