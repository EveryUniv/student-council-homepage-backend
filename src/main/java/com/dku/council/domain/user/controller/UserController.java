package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.request.RequestRefreshTokenDto;
import com.dku.council.domain.user.model.dto.request.RequestSignupDto;
import com.dku.council.domain.user.model.dto.response.ResponseLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseMajorDto;
import com.dku.council.domain.user.model.dto.response.ResponseRefreshTokenDto;
import com.dku.council.domain.user.service.UserService;
import com.dku.council.global.auth.role.UserOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "사용자", description = "사용자 인증 및 정보 관련 api")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final MessageSource messageSource;
    private final UserService service;

    /**
     * 회원가입
     * todo 이메일 인증으로 전환 + 회원가입 토큰 만료시간 정하기
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
     *
     * @param dto 요청 body
     * @return 로그인 인증 정보
     */
    @PostMapping("/login")
    public ResponseLoginDto login(@Valid @RequestBody RequestLoginDto dto) {
        return service.login(dto);
    }

    /**
     * 로그아웃
     * (현재 동작 안함) 서버에 같은 토큰으로 로그인 할 수 없게 로그아웃합니다.
     */
    @DeleteMapping
    public void logout() {
        // TODO Implementation
    }

    /**
     * 토큰 재발급
     *
     * @param dto 요청 body
     * @return 재발급된 로그인 인증 정보
     */
    @PostMapping("/reissue")
    @UserOnly
    public ResponseRefreshTokenDto refreshToken(HttpServletRequest request,
                                                @Valid @RequestBody RequestRefreshTokenDto dto) {
        return service.refreshToken(request, dto.getRefreshToken());
    }

    /**
     * 모든 학과 정보 가져오기
     */
    @GetMapping("/major")
    public List<ResponseMajorDto> getAllMajors() {
        // TODO Test it
        return Arrays.stream(MajorData.values())
                .filter(m -> !m.isSpecial())
                .map(m -> new ResponseMajorDto(m.name(), m.getName(messageSource)))
                .collect(Collectors.toList());
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/password")
    public void changePassword() {
        // TODO Implementation
    }
}
