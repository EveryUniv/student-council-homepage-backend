package com.dku.council.infra.captcha.service;

import com.dku.council.infra.captcha.model.Captcha;
import org.springframework.stereotype.Service;

// TODO Implement
@Service
public class CaptchaService {

    public Captcha requestCaptcha() {
        return new Captcha("key", "imageUrl");
    }

    public void verifyCaptcha(String captchaKey, String captchaValue) {
    }
}
