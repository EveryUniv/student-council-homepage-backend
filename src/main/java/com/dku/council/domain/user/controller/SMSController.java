package com.dku.council.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SMS 인증", description = "SMS 인증 관련 api")
@RestController
@RequestMapping("/user/sms")
@RequiredArgsConstructor
public class SMSController {

    /**
     * 사용자 인증 SMS 전송
     */
    @PostMapping
    public void sendVerificationSMS() {

    }

    /**
     * 인증 SMS 코드 확인
     */
    @PostMapping("/verify")
    public void verifySMSCode() {

    }
}
