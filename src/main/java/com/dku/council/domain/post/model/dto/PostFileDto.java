package com.dku.council.domain.post.model.dto;

import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class PostFileDto {

    @Schema(description = "파일 아이디", example = "1")
    private final Long id;

    @Schema(description = "파일 url", example = "http://1.2.3.4/1ddee68d-6afb-48d0-9cb6-04a8d8fea4ae.png")
    private final String url;

    @Schema(description = "썸네일 url (없으면 기본 이미지)", example = "http://1.2.3.4/thumb-1ddee68d-6afb-48d0-9cb6-04a8d8fea4ae.png")
    private final String thumbnailUrl;

    @Schema(description = "원본파일 이름", example = "my_image.png")
    private final String originalName;

    @Schema(description = "파일 타입", example = "image/jpeg")
    private final String mimeType;


    public PostFileDto(ObjectUploadContext context, PostFile file) {
        this.id = file.getId();
        this.url = context.getObjectUrl(file.getFileId());
        this.thumbnailUrl = context.getThumbnailUrl(file.getThumbnailId());
        this.originalName = file.getFileName();

        String fileMimeType = file.getMimeType();
        this.mimeType = Objects.requireNonNullElse(fileMimeType, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    public static List<PostFileDto> listOf(ObjectUploadContext context, List<PostFile> entities) {
        return entities.stream()
                .map(file -> new PostFileDto(context, file))
                .collect(Collectors.toList());
    }
}
