package com.dku.council.domain.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "단국대학교 학생 인증", description = "단국대학교 학생 인증 관련 api")
@RestController
@RequestMapping("/user/dku")
@RequiredArgsConstructor
public class DKUController {

    /**
     * 학생 정보 가져오기 (회원가입용)
     * 인증된 학생 정보를 가져옵니다. 학과의 경우 인식할 수 없으면 null이 반환됩니다.
     * 이 경우엔 학생에게 직접 학과를 입력받고, 회원가입시 입력받은 학과 정보를 같이 넘기면 됩니다.
     */
    @GetMapping
    public void getStudentInfo() {

    }

    /**
     * 단국대학교 학생 인증
     */
    @PostMapping("/verify")
    public void verifyDKUStudent() {

    }
}
