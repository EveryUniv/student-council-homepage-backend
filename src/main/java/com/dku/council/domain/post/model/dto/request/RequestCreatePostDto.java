package com.dku.council.domain.post.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreatePostDto {

    @NotBlank @Schema(description = "본문 제목", example = "제목")
    private final String title;

    @NotBlank @Schema(description = "본문", example = "내용")
    private final String body;
    @Schema(description = "첨부파일")
    private final List<MultipartFile> files;
}
