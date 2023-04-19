package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.InvalidCaptchaException;
import com.dku.council.infra.nhn.model.Captcha;

// TODO Implement
public class CaptchaService {

    public Captcha requestCaptcha() {
        return null;
    }

    public void verifyCaptcha(String captchaKey, String captchaValue) {
        throw new InvalidCaptchaException();
    }
}
