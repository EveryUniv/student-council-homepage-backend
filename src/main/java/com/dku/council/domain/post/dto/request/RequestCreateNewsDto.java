package com.dku.council.domain.post.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestCreateNewsDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String body;
}
