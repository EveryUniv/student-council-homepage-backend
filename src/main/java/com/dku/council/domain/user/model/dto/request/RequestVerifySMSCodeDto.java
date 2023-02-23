package com.dku.council.domain.user.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = {@JsonCreator})
public class RequestVerifySMSCodeDto {

    @NotBlank
    private final String code;
}
