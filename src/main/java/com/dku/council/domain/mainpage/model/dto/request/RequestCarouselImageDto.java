package com.dku.council.domain.mainpage.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class RequestCarouselImageDto {

    @NotNull
    @Schema(description = "이미지 파일 등록")
    private final MultipartFile imageFile;

    @NotEmpty
    @Schema(description = "이미지 리다이렉트 경로", example = "redirect.com/redirectPath")
    private final String redirectUrl;

}
