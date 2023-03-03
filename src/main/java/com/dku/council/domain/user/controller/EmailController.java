package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.dto.request.RequestSendEmailCode;
import com.dku.council.domain.user.model.dto.request.RequestVerifyEmailCodeDto;
import com.dku.council.domain.user.model.dto.response.ResponseStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.service.DkuEmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "단국대학교 학생 인증-email", description = "단국대학교 학생 인증 관련 api")
@RestController
@RequestMapping("/user/email")
@RequiredArgsConstructor
public class EmailController {
    private final DkuEmailService service;

    @PostMapping
    public void sendEmailCode(@Valid @RequestBody RequestSendEmailCode dto){
        service.sendEmailCode(dto);
    }

    @PostMapping("/verify")
    public ResponseVerifyStudentDto verifyDKUStudent(@Valid @RequestBody RequestVerifyEmailCodeDto dto){
        return service.validateEmailCode(dto);
    }


}
