package com.dku.council.domain.user.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLoginDto {

    @NotBlank
    private final String studentId;

    @NotBlank
    private final String password;
}
