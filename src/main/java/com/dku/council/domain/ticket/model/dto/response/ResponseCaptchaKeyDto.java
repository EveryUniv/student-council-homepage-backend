package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseCaptchaKeyDto {

    @Schema(description = "Captcha 키")
    private final String key;

    public ResponseCaptchaKeyDto(String captchaKey) {
        this.key = captchaKey;
    }
}
