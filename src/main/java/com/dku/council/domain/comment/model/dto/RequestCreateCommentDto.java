package com.dku.council.domain.comment.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestCreateCommentDto {
    @NotBlank
    private final String text;
}
