package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.dto.request.RequestVerifySMSCodeDto;
import com.dku.council.domain.user.model.dto.request.RequestWithPhoneNumberDto;
import com.dku.council.domain.user.service.SMSVerificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "SMS 인증", description = "SMS 인증 관련 api")
@RestController
@RequestMapping("/user/sms")
@RequiredArgsConstructor
public class SMSController {

    private final SMSVerificationService service;

    /**
     * 사용자 인증 SMS 전송
     *
     * @param dto         요청 body
     * @param signupToken 회원가입 토큰
     */
    @PostMapping("/{signup-token}")
    public void sendVerificationSMS(@Valid @RequestBody RequestWithPhoneNumberDto dto,
                                    @PathVariable("signup-token") String signupToken) {
        service.sendSMSCode(signupToken, dto.getPhoneNumber());
    }

    /**
     * 인증 SMS 코드 확인
     *
     * @param dto         요청 body
     * @param signupToken 회원가입 토큰
     */
    @PostMapping("/verify/{signup-token}")
    public void verifySMSCode(@Valid @RequestBody RequestVerifySMSCodeDto dto,
                              @PathVariable("signup-token") String signupToken) {
        service.verifySMSCode(signupToken, dto.getCode());
    }
}
