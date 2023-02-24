package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.dto.request.RequestVerifyStudentDto;
import com.dku.council.domain.user.model.dto.response.ResponseStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.service.DKUAuthService;
import com.dku.council.infra.dku.model.StudentInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "단국대학교 학생 인증", description = "단국대학교 학생 인증 관련 api")
@RestController
@RequestMapping("/user/dku")
@RequiredArgsConstructor
public class DKUController {

    private final MessageSource messageSource;
    private final DKUAuthService service;

    /**
     * 단국대학교 학생 인증
     * 학생 인증을 진행합니다. 성공시 반환되는 토큰은 회원가입에 사용됩니다.
     *
     * @param dto 요청 body
     */
    @PostMapping("/verify")
    public ResponseVerifyStudentDto verifyDKUStudent(@Valid @RequestBody RequestVerifyStudentDto dto) {
        return service.verifyStudent(dto);
    }

    /**
     * 학생 정보 가져오기 (회원가입용)
     * 회원가입 토큰을 통해 인증된 학생 정보를 가져옵니다. 회원가입하면 어차피 학생 정보도 반환해주긴 합니다.
     * 회원가입시 반환되는 response 사용하기 / 이 api사용하기 둘 중에서 편하신 방법으로 api를 사용하세요.
     *
     * @param signupToken 회원가입 토큰
     */
    @GetMapping("/{signup-token}")
    public ResponseStudentInfoDto getStudentInfo(@PathVariable("signup-token") String signupToken) {
        StudentInfo studentInfo = service.getStudentInfo(signupToken);
        return ResponseStudentInfoDto.from(messageSource, studentInfo);
    }
}
