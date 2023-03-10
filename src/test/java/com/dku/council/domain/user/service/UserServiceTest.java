package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.LoginUserNotFoundException;
import com.dku.council.domain.user.exception.WrongPasswordException;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseUserInfoDto;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtAuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService service;


    @Test
    @DisplayName("로그인")
    void login() {
        // given
        User user = UserMock.create();
        RequestLoginDto dto = new RequestLoginDto(user.getStudentId(), user.getPassword());
        AuthenticationToken auth = JwtAuthenticationToken.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        when(userRepository.findByStudentId(dto.getStudentId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.issue(user)).thenReturn(auth);

        // when
        ResponseLoginDto response = service.login(dto);

        // then
        assertThat(response.getStudentId()).isEqualTo(user.getStudentId());
        assertThat(response.getUserName()).isEqualTo(user.getName());
        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        assertThat(response.isAdmin()).isEqualTo(false);
    }

    @Test
    @DisplayName("로그인 실패 - 찾을 수 없는 아이디")
    void failedLoginByNotFoundId() {
        // given
        RequestLoginDto dto = new RequestLoginDto("id", "pwd");
        when(userRepository.findByStudentId(dto.getStudentId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(LoginUserNotFoundException.class, () ->
                service.login(dto));
    }

    @Test
    @DisplayName("로그인 실패 - 틀린 비밀번호")
    void failedLoginByWrongPwd() {
        // given
        User user = UserMock.create();
        RequestLoginDto dto = new RequestLoginDto(user.getStudentId(), user.getPassword());

        when(userRepository.findByStudentId(dto.getStudentId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(WrongPasswordException.class, () ->
                service.login(dto));
    }

    @Test
    @DisplayName("토큰 재발급")
    void refreshToken() {
        // given
        AuthenticationToken token = JwtAuthenticationToken.builder()
                .accessToken("newaccess")
                .refreshToken("refresh")
                .build();
        when(jwtProvider.getAccessTokenFromHeader(null)).thenReturn("access");
        when(jwtProvider.reissue("access", "refresh"))
                .thenReturn(token);

        // when
        service.refreshToken(null, "refresh");

        // then
        assertThat(token.getAccessToken()).isEqualTo("newaccess");
        assertThat(token.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    @DisplayName("내 정보 가져오기")
    void getUserInfo() {
        // given
        User user = UserMock.create();
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Major");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        ResponseUserInfoDto info = service.getUserInfo(user.getId());

        // then
        assertThat(info.getMajor()).isEqualTo("Major");
        assertThat(info.getStudentName()).isEqualTo(user.getName());
        assertThat(info.getYearOfAdmission()).isEqualTo(user.getYearOfAdmission().toString());
    }

    @Test
    @DisplayName("내 정보 가져오기 실패 - 찾을 수 없는 아이디")
    void failedGetUserInfoByNotFound() {
        // given
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        // when
        assertThrows(LoginUserNotFoundException.class, () ->
                service.getUserInfo(0L));
    }
}