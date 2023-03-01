package com.dku.council.domain.comment.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
public class RequestCreateCommentDto {
    @NotBlank
    private final String text;
}
