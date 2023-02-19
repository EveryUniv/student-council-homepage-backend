package com.dku.council.domain.post.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateNewsDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String body;
}
