package com.dku.council.infra.naver.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class ResponseCaptchaKey {
    private final String key;
}
