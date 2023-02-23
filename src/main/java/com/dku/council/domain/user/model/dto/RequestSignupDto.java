package com.dku.council.domain.user.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestSignupDto {

    @NotBlank
    private final String password;
}
