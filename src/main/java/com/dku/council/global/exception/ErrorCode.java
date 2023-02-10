package com.dku.council.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 필요한 요청입니다."),
    NOT_GRANTED(HttpStatus.FORBIDDEN, "해당 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.NOT_ACCEPTABLE, "만료된 토큰입니다."),
    INVALID_TYPE(HttpStatus.NOT_ACCEPTABLE, "잘못된 형식입니다.");

    private final HttpStatus status;
    private final String message;


}
