package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.LoginUserNotFoundException;
import com.dku.council.domain.user.exception.WrongPasswordException;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseMajorDto;
import com.dku.council.domain.user.model.dto.response.ResponseRefreshTokenDto;
import com.dku.council.domain.user.model.dto.response.ResponseUserInfoDto;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.global.auth.role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserFindService {

    public void sendSMSId(String phone) {

    }

    public void sendPwdSMSCode(String phone) {

    }

    public void verifyPwdSMSCode(String phone, String code) {

    }
}
