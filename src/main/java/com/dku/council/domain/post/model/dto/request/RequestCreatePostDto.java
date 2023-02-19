package com.dku.council.domain.post.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreatePostDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String body;

    private final List<MultipartFile> files;
}
