package com.dku.council.domain.user.controller;

import com.dku.council.domain.user.model.DkuUserInfo;
import com.dku.council.domain.user.model.dto.request.RequestDkuStudentDto;
import com.dku.council.domain.user.model.dto.response.ResponseScrappedStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.service.DKUAuthService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "단국대학교 학생 인증", description = "단국대학교 학생 인증 관련 api")
@RestController
@RequestMapping("/user/dku")
@RequiredArgsConstructor
public class DKUController {

    private final DKUAuthService service;

    /**
     * 단국대학교 학생 인증
     * <p>학생 인증을 진행합니다. 성공시 반환되는 토큰은 회원가입에 사용됩니다.</p>
     *
     * @param dto 요청 body
     */
    @PostMapping("/verify")
    public ResponseVerifyStudentDto verifyDKUStudent(@Valid @RequestBody RequestDkuStudentDto dto) {
        return service.verifyStudent(dto);
    }

    /**
     * 단국대학교 학생 정보 업데이트
     * <p>단국대학교 사이트에서 학생 정보를 다시 가져와 업데이트합니다.</p>
     *
     * @param dto 요청 body
     */
    @PatchMapping
    @UserAuth
    public ResponseScrappedStudentInfoDto updateDKUStudent(AppAuthentication auth,
                                                           @Valid @RequestBody RequestDkuStudentDto dto) {
        return service.updateDKUStudent(auth.getUserId(), dto);
    }

    /**
     * 학생 정보 가져오기 (회원가입용)
     * <p>회원가입 토큰을 통해 인증된 학생 정보를 가져옵니다. 회원가입하면 어차피 학생 정보도 반환해주긴 합니다.
     * 회원가입시 반환되는 response 사용하기 / 이 api사용하기 둘 중에서 편하신 방법으로 api를 사용하세요.</p>
     *
     * @param signupToken 회원가입 토큰
     */
    @GetMapping("/{signup-token}")
    public ResponseScrappedStudentInfoDto getStudentInfo(@PathVariable("signup-token") String signupToken) {
        DkuUserInfo studentInfo = service.getStudentInfo(signupToken);
        return new ResponseScrappedStudentInfoDto(studentInfo);
    }
}
