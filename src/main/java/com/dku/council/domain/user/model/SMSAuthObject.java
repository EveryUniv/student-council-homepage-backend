package com.dku.council.domain.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SMSAuthObject {
    private final String phone;
    private final String code;
}
