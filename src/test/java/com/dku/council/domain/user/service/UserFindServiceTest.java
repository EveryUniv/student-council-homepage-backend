package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.NotSMSAuthorizedException;
import com.dku.council.domain.user.exception.WrongSMSCodeException;
import com.dku.council.domain.user.model.SMSAuth;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserFindRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.service.SMSService;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.Optional;

import static com.dku.council.domain.user.service.UserFindService.CODE_AUTH_COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFindServiceTest {

    private final Clock clock = ClockUtil.create();

    @Mock
    private SMSService smsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFindRepository userFindRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserFindService service;

    @BeforeEach
    void setUp() {
        service = new UserFindService(clock, smsService, userRepository, userFindRepository,
                messageSource, passwordEncoder, 6);
    }


    @Test
    @DisplayName("아이디 찾기 - 휴대폰 번호로 정확한 메시지를 보내는가")
    void sendIdBySMS() {
        // given
        String phone = "01012345678";
        User user = UserMock.createDummyMajor();
        when(messageSource.getMessage(eq("sms.find.id-message"),
                argThat(arg -> arg.length == 1 && arg[0].equals(user.getStudentId())), any()))
                .thenReturn("Message");
        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(user));

        // when
        service.sendIdBySMS(phone);

        // then
        verify(smsService).sendSMS(phone, "Message");
    }

    @Test
    @DisplayName("비밀번호 찾기 - 휴대폰 번호로 정확한 메시지를 보내는가")
    void sendPwdCodeBySMS() {
        // given
        User user = UserMock.createDummyMajor();
        when(userRepository.findByPhone(user.getPhone())).thenReturn(Optional.of(user));
        when(messageSource.getMessage(eq("sms.find.pwd-auth-message"), any(), any())).thenReturn("Message");

        // when
        service.sendPwdCodeBySMS(user.getPhone());

        // then
        verify(smsService).sendSMS(user.getPhone(), "Message");
    }

    @Test
    @DisplayName("비밀번호 찾기 - 유저가 없으면 오류")
    void failedSendPwdCodeBySMSByNotFoundUser() {
        // given
        User user = UserMock.createDummyMajor();
        when(userRepository.findByPhone(user.getPhone())).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThrows(UserNotFoundException.class, () ->
                service.sendPwdCodeBySMS(user.getPhone()));
    }

    @Test
    @DisplayName("토큰 검증")
    void verifyPwdCode() {
        // given
        User user = UserMock.createDummyMajor();
        SMSAuth auth = new SMSAuth(user.getPhone(), "CODE");
        String token = "token";
        when(userFindRepository.getAuthCode(eq(token), any())).thenReturn(Optional.of(auth));

        // when
        service.verifyPwdCode(token, auth.getCode());

        // then
        verify(userFindRepository).setAuthCode(
                eq(token),
                eq(CODE_AUTH_COMPLETED),
                eq(auth.getPhone()),
                any());
    }

    @Test
    @DisplayName("토큰 검증 - auth code가 다르면 오류")
    void failedVerifyPwdCodeByWrongAuthCode() {
        // given
        User user = UserMock.createDummyMajor();
        SMSAuth auth = new SMSAuth(user.getPhone(), "CODE");
        String token = "token";
        when(userFindRepository.getAuthCode(eq(token), any())).thenReturn(Optional.of(auth));

        // when & then
        assertThrows(WrongSMSCodeException.class, () ->
                service.verifyPwdCode(token, "WRONG_CODE"));
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword() {
        // given
        String token = "token";
        String password = "password";
        SMSAuth auth = new SMSAuth("phone", CODE_AUTH_COMPLETED);
        User user = UserMock.createDummyMajor();

        when(userFindRepository.getAuthCode(eq(token), any())).thenReturn(Optional.of(auth));
        when(userRepository.findByPhone(auth.getPhone())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        service.changePassword(token, password);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        verify(userFindRepository).deleteAuthCode(token);
    }

    @Test
    @DisplayName("비밀번호 변경 - 토큰 인증 안한경우")
    void failedChangePasswordByNoTokenAuth() {
        // given
        String token = "token";
        String password = "password";
        SMSAuth auth = new SMSAuth("phone", "123456");

        when(userFindRepository.getAuthCode(eq(token), any())).thenReturn(Optional.of(auth));

        // when & then
        assertThrows(NotSMSAuthorizedException.class, () ->
                service.changePassword(token, password));
    }
}