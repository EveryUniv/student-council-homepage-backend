package com.dku.council.domain.tag.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestCreateTagDto {

    @NotBlank
    private String name;
}
