package com.dku.council.domain.ticket.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class RequestEnrollDto {

    @NotNull
    @Schema(description = "참여 이벤트 ID")
    private final Long eventId;

    @NotEmpty
    @Schema(description = "Captcha 키")
    private final String captchaKey;

    @NotEmpty
    @Schema(description = "Captcha 값")
    private final String captchaValue;
}
