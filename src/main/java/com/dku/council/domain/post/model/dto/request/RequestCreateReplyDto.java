package com.dku.council.domain.post.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestCreateReplyDto {
    @NotBlank
    private final String answer;
}
