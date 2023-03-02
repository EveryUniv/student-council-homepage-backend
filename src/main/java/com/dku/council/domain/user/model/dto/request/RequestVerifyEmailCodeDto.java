package com.dku.council.domain.user.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = {@JsonCreator})
public class RequestVerifyEmailCodeDto {

    @NotBlank
    @Pattern(regexp = "^\\d{5}$")
    private final String emailCode;
}
