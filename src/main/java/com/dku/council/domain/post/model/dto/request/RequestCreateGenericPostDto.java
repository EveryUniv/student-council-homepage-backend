package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class RequestCreateGenericPostDto<T> {

    @NotBlank
    @Schema(description = "제목", example = "제목")
    private final String title;

    @NotBlank
    @Schema(description = "본문", example = "내용")
    private final String body;

    @Schema(description = "태그 ID 목록")
    private final List<Long> tagIds;

    @Schema(description = "첨부파일")
    private final List<MultipartFile> files;


    public RequestCreateGenericPostDto(String title, String body, List<Long> tagIds, List<MultipartFile> files) {
        this.title = title;
        this.body = body;
        this.tagIds = Objects.requireNonNullElseGet(tagIds, ArrayList::new);
        this.files = Objects.requireNonNullElseGet(files, ArrayList::new);
    }

    public abstract T toEntity(User user);
}
