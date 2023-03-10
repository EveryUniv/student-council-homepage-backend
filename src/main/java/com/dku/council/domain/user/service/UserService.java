package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.LoginUserNotFoundException;
import com.dku.council.domain.user.exception.WrongPasswordException;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.request.RequestSignupDto;
import com.dku.council.domain.user.model.dto.response.ResponseLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseMajorDto;
import com.dku.council.domain.user.model.dto.response.ResponseRefreshTokenDto;
import com.dku.council.domain.user.model.dto.response.ResponseUserInfoDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

// TODO Test it
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final SMSVerificationService smsVerificationService;
    private final DKUAuthService dkuAuthService;
    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    @Transactional
    public void signup(RequestSignupDto dto, String signupToken) {
        StudentInfo studentInfo = dkuAuthService.getStudentInfo(signupToken);
        String phone = smsVerificationService.getPhoneNumber(signupToken);
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        Major major = retrieveMajor(studentInfo.getMajorName(), studentInfo.getDepartmentName());

        User user = User.builder()
                .studentId(studentInfo.getStudentId())
                .password(encryptedPassword)
                .name(studentInfo.getStudentName())
                .phone(phone)
                .major(major)
                .yearOfAdmission(studentInfo.getYearOfAdmission())
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        deleteSignupAuths(signupToken);
    }

    private Major retrieveMajor(String majorName, String departmentName) {
        return majorRepository.findByName(majorName, departmentName)
                .orElseGet(() -> {
                    Major entity = new Major(majorName, departmentName);
                    entity = majorRepository.save(entity);
                    return entity;
                });
    }

    private void deleteSignupAuths(String signupToken) {
        if (!dkuAuthService.deleteStudentAuth(signupToken)) {
            log.error("Can't delete user signup authentication: dku student auth");
        }
        if (!smsVerificationService.deleteSMSAuth(signupToken)) {
            log.error("Can't delete user signup authentication: sms auth");
        }
    }

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
}
