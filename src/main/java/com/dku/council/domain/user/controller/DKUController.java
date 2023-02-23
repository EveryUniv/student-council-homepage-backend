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
     * 회원가입용 학생 정보 가져오기
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
