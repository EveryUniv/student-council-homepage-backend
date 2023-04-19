package com.dku.council.domain.ticket.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@RequiredArgsConstructor
public class RequestEnrollDto {

    @NotEmpty
    @Schema(description = "Captcha 키")
    private final String captchaKey;

    @NotEmpty
    @Schema(description = "Captcha 값")
    private final String captchaValue;
}
