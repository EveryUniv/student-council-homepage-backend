package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.dto.request.*;
import com.dku.council.domain.user.model.dto.response.*;
import com.dku.council.domain.user.service.SignupService;
import com.dku.council.domain.user.service.UserFindService;
import com.dku.council.domain.user.service.UserService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "사용자", description = "사용자 인증 및 정보 관련 api")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserFindService userFindService;
    private final SignupService signupService;


    /**
     * 내 정보 조회
     *
     * @return 내 정보
     */
    @GetMapping
    @UserOnly
    public ResponseUserInfoDto getMyInfo(AppAuthentication auth) {
        return userService.getUserInfo(auth.getUserId());
    }

    /**
     * 아이디(학번) 찾기
     *
     * @param dto 요청 body
     */
    @PostMapping("/find/id")
    public void sendIdBySMS(@Valid @RequestBody RequestWithPhoneNumberDto dto) {
        userFindService.sendIdBySMS(dto.getPhoneNumber());
    }

    /**
     * 비밀번호 재설정 코드 전송.
     * SMS인증 코드 전송 -> 인증 코드 확인(응답으로 비번 변경 토큰) -> 비밀번호 변경 순으로 흘러갑니다.
     *
     * @param dto 요청 body
     * @return 비밀번호 재설정 토큰
     */
    @PostMapping("/find/pwd")
    public ResponsePasswordChangeTokenDto sendPwdCodeBySMS(@Valid @RequestBody RequestSendPasswordFindCodeDto dto) {
        return userFindService.sendPwdCodeBySMS(dto.getStudentId(), dto.getPhoneNumber());
    }

    /**
     * 비밀번호 재설정 인증 코드 확인
     *
     * @param dto 요청 body
     */
    @PostMapping("/find/pwd/verify")
    public void verifyPwdCodeBySMS(@Valid @RequestBody RequestVerifyPwdSMSCodeDto dto) {
        userFindService.verifyPwdCode(dto.getToken(), dto.getCode());
    }

    /**
     * 비밀번호 변경.
     * 변경 전에 재설정 인증 코드 확인을 해야 합니다.
     *
     * @param dto 요청 body
     */
    @PatchMapping("/find/pwd/reset")
    public void changePassword(@Valid @RequestBody RequestPasswordChangeDto dto) {
        userFindService.changePassword(dto.getToken(), dto.getPassword());
    }

    /**
     * 닉네임 변경.
     *
     * @param auth
     * @param dto
     */
    @PatchMapping("/change/nickname")
    @UserOnly
    public void changeNickName(AppAuthentication auth, @Valid @RequestBody RequestNickNameChangeDto dto){
        userService.changeNickName(auth.getUserId(), dto);
    }

    /**
     * 회원가입
     *
     * @param dto         요청 Body
     * @param signupToken 회원가입 토큰
     */
    @PostMapping("/{signup-token}")
    public void signup(@Valid @RequestBody RequestSignupDto dto,
                       @PathVariable("signup-token") String signupToken) {
        signupService.signup(dto, signupToken);
    }

    /**
     * 닉네임이 이미 존재하는지 검증.
     * 닉네임이 이미 존재하는지 검증합니다. 만약 존재하지 않으면 OK를 반환하고,
     * 이미 사용중인 경우에는 BAD_REQUEST 오류가 발생합니다.
     *
     * @param nickname 닉네임
     */
    @GetMapping("/valid")
    public void validNickname(@RequestParam String nickname) {
        signupService.checkAlreadyNickname(nickname);
    }

    /**
     * 로그인
     *
     * @param dto 요청 body
     * @return 로그인 인증 정보
     */
    @PostMapping("/login")
    public ResponseLoginDto login(@Valid @RequestBody RequestLoginDto dto) {
        return userService.login(dto);
    }

    /**
     * 로그아웃 (현재 동작 안함)
     * 서버에 같은 토큰으로 로그인 할 수 없게 로그아웃합니다.
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
        return userService.refreshToken(request, dto.getRefreshToken());
    }

    /**
     * 모든 학과 정보 가져오기
     */
    @GetMapping("/major")
    public List<ResponseMajorDto> getAllMajors() {
        return userService.getAllMajors();
    }
}
