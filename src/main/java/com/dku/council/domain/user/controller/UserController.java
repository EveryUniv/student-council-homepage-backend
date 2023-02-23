package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.dto.request.RequestSignupDto;
import com.dku.council.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "사용자", description = "사용자 인증 및 정보 관련 api")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    /**
     * 회원가입
     *
     * @param dto         요청 Body
     * @param signupToken 회원가입 토큰
     */
    @PostMapping("/{signup-token}")
    public void signup(@Valid @RequestBody RequestSignupDto dto,
                       @PathVariable("signup-token") String signupToken) {
        service.signup(dto, signupToken);
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public void login() {
        // TODO Implementation
    }

    /**
     * 로그아웃
     * 서버에 같은 토큰으로 로그인 할 수 없게 로그아웃합니다.
     */
    @DeleteMapping
    public void logout() {
        // TODO Implementation
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/reissue")
    public void refreshToken() {
        // TODO Implementation
    }

    /**
     * 모든 학과 정보 가져오기
     */
    @GetMapping("/major")
    public void getAllMajors() {
        // TODO Implementation
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/password")
    public void changePassword() {
        // TODO Implementation
    }
}
