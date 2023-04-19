package com.dku.council.infra.nhn.model;

import lombok.Getter;

@Getter
public class Captcha {
    private final String key;
    private final String imageUrl;

    public Captcha(String key, String imageUrl) {
        this.key = key;
        this.imageUrl = imageUrl;
    }
}
