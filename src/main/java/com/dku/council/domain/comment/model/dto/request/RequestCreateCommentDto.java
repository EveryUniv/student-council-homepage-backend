package com.dku.council.domain.comment.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateCommentDto {
    @NotBlank(message = "댓글을 입력해주세요.")
    private String text;
}
