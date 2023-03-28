package com.dku.council.domain.user.controller;

import com.dku.council.domain.comment.model.dto.CommentedPostResponseDto;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.user.model.dto.request.*;
import com.dku.council.domain.user.model.dto.response.*;
import com.dku.council.domain.user.service.*;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final MyPostService myPostService;
    private final MyCommentedPostService myCommentedPostService;
    private final MyLikedPostService myLikedPostService;

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
     * 아이디(학번) 찾기.
     * 휴대폰 번호를 보내면 SMS로 학번 전송
     *
     * @param dto 요청 body
     */
    @PostMapping("/find/id")
    public void sendIdBySMS(@Valid @RequestBody RequestWithPhoneNumberDto dto) {
        userFindService.sendIdBySMS(dto.getPhoneNumber());
    }

    /**
     * 비밀번호 재설정 인증 코드 전송 (1)
     * <p>재설정 코드(6자리) SMS로 전송 -> 재설정 토큰(UUID) 응답.</p> 비밀번호 재설정 플로우는 SMS인증 코드 전송 ->
     * 인증 코드 확인 -> 비밀번호 변경 순으로 흘러갑니다.
     *
     * @param dto 요청 body
     * @return 비밀번호 재설정 토큰
     */
    @PostMapping("/find/pwd")
    public ResponseChangeTokenDto sendPwdCodeBySMS(@Valid @RequestBody RequestWithPhoneNumberDto dto) {
        return userFindService.sendPwdCodeBySMS(dto.getPhoneNumber());
    }

    /**
     * 비밀번호 재설정 인증 코드 확인 (2)
     * <p>재설정 토큰과 재설정 코드로 본인 인증을 합니다.</p>
     *
     * @param dto 요청 body
     */
    @PostMapping("/find/pwd/verify")
    public void verifyPwdCodeBySMS(@Valid @RequestBody RequestVerifyTokenCodeDto dto) {
        userFindService.verifyPwdCode(dto.getToken(), dto.getCode());
    }

    /**
     * 비밀번호 변경 (3)
     * <p>재설정 토큰과 새로운 비밀번호 -> 비밀번호 변경 완료.</p>
     * 변경 전에 반드시 '재설정 인증 코드 확인'을 해야 합니다.
     *
     * @param dto 요청 body
     */
    @PatchMapping("/find/pwd/reset")
    public void changePassword(@Valid @RequestBody RequestPasswordChangeDto dto) {
        userFindService.changePassword(dto.getToken(), dto.getPassword());
    }

    /**
     * 휴대폰 재설정 인증 코드 전송 (1)
     * <p>재설정 코드(6자리) SMS로 전송 -> 재설정 토큰(UUID) 응답.</p> 핸드폰 번호 재설정 플로우는 SMS인증 코드 전송 ->
     * 인증 코드 확인 & 핸드폰 번호 변경 순으로 흘러갑니다.
     *
     * @param dto 요청 body
     * @return 핸드폰 번호 재설정 토큰
     */
    @PostMapping("/change/phone/verify")
    @UserOnly
    public ResponseChangeTokenDto sendChangePhoneCodeBySMS(AppAuthentication auth, @Valid @RequestBody RequestWithPhoneNumberDto dto){
        return userFindService.sendChangePhoneCodeBySMS(auth.getUserId(), dto.getPhoneNumber());
    }

    /**
     * 휴대폰 재설정 인증 코드 확인 (2)
     * <p>재설정 토큰과 재설정 코드로 요청받은 번호로 핸드폰 번호 변경 합니다.</p>
     * @param dto 요청 body
     */
    @PatchMapping("/change/phone")
    @UserOnly
    public void changePhoneNumber(AppAuthentication auth, @Valid @RequestBody RequestVerifyTokenCodeDto dto){
        userFindService.changePhoneNumber(auth.getUserId(), dto.getToken(), dto.getCode());
    }


    /**
     * 닉네임 변경.
     *
     * @param dto 요청 body
     */
    @PatchMapping("/change/nickname")
    @UserOnly
    public void changeNickName(AppAuthentication auth, @Valid @RequestBody RequestNickNameChangeDto dto){
        userService.changeNickName(auth.getUserId(), dto);
    }

    /**
     * 비밀번호 변경 - 기존 비밀번호를 알고 있는 경우
     * @param dto 요청 body
     */
    @PatchMapping("/change/password")
    @UserOnly
    public void changeExistPassword(AppAuthentication auth, @Valid @RequestBody RequestExistPasswordChangeDto dto){
        userService.changePassword(auth.getUserId(), dto);
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

    /**
     * 내가 쓴 글 모두 조회하기
     */
    @GetMapping("/post")
    @UserOnly
    public ResponsePage<SummarizedGenericPostDto> listMyPosts(AppAuthentication auth,
                                                              @ParameterObject Pageable pageable,
                                                              @RequestParam(defaultValue = "50") int bodySize) {
        Page<SummarizedGenericPostDto> posts = myPostService.listMyPosts(auth.getUserId(), pageable, bodySize);
        return new ResponsePage<>(posts);
    }

    /**
     * 내가 댓글 단 글들 모두 조회하기
     */
    @GetMapping("/post/commented")
    @UserOnly
    public ResponsePage<CommentedPostResponseDto> listMyCommentedPosts(AppAuthentication auth,
                                                                       @ParameterObject Pageable pageable) {
        Page<CommentedPostResponseDto> commentedPosts = myCommentedPostService.listMyCommentedPosts(auth.getUserId(), pageable);
        return new ResponsePage<>(commentedPosts);
    }

    /**
     * 내가 좋아요 한 글들 모두 조회하기
     */
    @GetMapping("/post/liked")
    @UserOnly
    public ResponsePage<SummarizedGenericPostDto> listMyLikedPosts(AppAuthentication auth,
                                                                   @ParameterObject Pageable pageable,
                                                                   @RequestParam(defaultValue = "50") int bodySize) {
        Page<SummarizedGenericPostDto> likedPosts = myLikedPostService.listMyLikedPosts(auth.getUserId(), pageable, bodySize);
        return new ResponsePage<>(likedPosts);
    }
}
