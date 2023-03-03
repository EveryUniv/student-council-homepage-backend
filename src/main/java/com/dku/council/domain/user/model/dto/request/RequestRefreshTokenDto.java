package com.dku.council.domain.user.model.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
public class RequestRefreshTokenDto {

    @NotBlank
    private final String refreshToken;
}
