package com.dku.council.domain.ticket.model.dto;

import com.dku.council.infra.nhn.model.Captcha;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CaptchaDto {

    @Schema(description = "Captcha 키")
    private final String key;

    @Schema(description = "Captcha 이미지 URL")
    private final String imageUrl;

    public CaptchaDto(Captcha captcha) {
        this.key = captcha.getKey();
        this.imageUrl = captcha.getImageUrl();
    }
}
