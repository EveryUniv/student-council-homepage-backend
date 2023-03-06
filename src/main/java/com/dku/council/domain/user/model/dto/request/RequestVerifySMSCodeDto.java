package com.dku.council.domain.user.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestVerifySMSCodeDto {

    @NotBlank
    private final String code;
}
