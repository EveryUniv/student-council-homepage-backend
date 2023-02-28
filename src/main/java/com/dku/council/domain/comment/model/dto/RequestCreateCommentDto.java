package com.dku.council.domain.comment.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateCommentDto {
    @NotBlank
    private String text;
}
