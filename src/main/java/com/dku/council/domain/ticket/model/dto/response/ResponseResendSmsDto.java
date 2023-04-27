package com.dku.council.domain.ticket.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseResendSmsDto {

    @Schema(description = "재전송된 인증 번호", example = "123456")
    private final String code;

    public ResponseResendSmsDto(String code) {
        this.code = code;
    }
}
