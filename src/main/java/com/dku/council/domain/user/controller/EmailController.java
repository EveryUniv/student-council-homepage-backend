package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.DkuUserInfo;
import com.dku.council.domain.user.model.dto.request.RequestSendEmailCode;
import com.dku.council.domain.user.model.dto.request.RequestVerifyEmailCodeDto;
import com.dku.council.domain.user.model.dto.response.ResponseScrappedStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.service.DkuEmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "단국대학교 학생 인증-email", description = "단국대학교 학생 인증 관련 api")
//@RestController
@RequestMapping("/user/email")
@RequiredArgsConstructor
public class EmailController {
    private final DkuEmailService service;

    /**
     * 단국대학교 학생 이메일로 인증번호를 보냅니다.
     *
     * @param dto 요청 body (8자리 학번)
     */
    @PostMapping
    public void sendEmailCode(@Valid @RequestBody RequestSendEmailCode dto) {
        service.sendEmailCode(dto);
    }

    /**
     * 단국대학교 학생 이메일 인증.
     * 성공시 반환되는 토큰은 회원가입시 사용됩니다.
     *
     * @param dto 요청 body
     * @return 회원가입용 토큰 및 학생 정보
     */
    @PostMapping("/verify")
    public ResponseVerifyStudentDto verifyDKUStudent(@Valid @RequestBody RequestVerifyEmailCodeDto dto) {
        return service.validateEmailCode(dto);
    }

    /**
     * 학생 정보 가져오기 (회원가입용)
     * 회원가입 토큰을 통해 인증된 학생 정보를 가져옵니다. 회원가입하면 어차피 학생 정보도 반환해주긴 합니다.
     * 회원가입시 반환되는 response 사용하기 / 이 api사용하기 둘 중에서 편하신 방법으로 api를 사용하세요.
     *
     * @param signupToken 회원가입 토큰
     */
    @GetMapping("/{signup-token}")
    public ResponseScrappedStudentInfoDto getStudentInfo(@PathVariable("signup-token") String signupToken) {
        DkuUserInfo info = service.getStudentInfo(signupToken);
        return new ResponseScrappedStudentInfoDto(info);
    }


}
