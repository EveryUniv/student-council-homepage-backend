package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.LoginUserNotFoundException;
import com.dku.council.domain.user.exception.WrongPasswordException;
import com.dku.council.domain.user.model.dto.request.RequestExistPasswordChangeDto;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.request.RequestNickNameChangeDto;
import com.dku.council.domain.user.model.dto.request.RequestVerifySMSCodeDto;
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
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ResponseLoginDto login(RequestLoginDto dto) {
        User user = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(LoginUserNotFoundException::new);

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            UserRole role = user.getUserRole();
            AuthenticationToken token = jwtProvider.issue(user);
            return new ResponseLoginDto(token, role, user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public ResponseRefreshTokenDto refreshToken(HttpServletRequest request, String refreshToken) {
        String accessToken = jwtProvider.getAccessTokenFromHeader(request);
        AuthenticationToken token = jwtProvider.reissue(accessToken, refreshToken);
        return new ResponseRefreshTokenDto(token);
    }

    public ResponseUserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(LoginUserNotFoundException::new);

        String year = user.getYearOfAdmission().toString();
        String major = user.getMajor().getName();
        return new ResponseUserInfoDto(user.getName(), year, major);
    }

    public List<ResponseMajorDto> getAllMajors() {
        return majorRepository.findAll().stream()
                .map(ResponseMajorDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeNickName(Long userId, RequestNickNameChangeDto dto) {
        User user = userRepository.findById(userId).orElseThrow(LoginUserNotFoundException::new);
        user.changeNickName(dto.getNickname());
    }

    @Transactional
    public void changePassword(Long userId, RequestExistPasswordChangeDto dto) {
        User user = userRepository.findById(userId).orElseThrow(LoginUserNotFoundException::new);
        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            user.changePassword(encodedPassword);
        } else {
            throw new WrongPasswordException();
        }
    }

}
