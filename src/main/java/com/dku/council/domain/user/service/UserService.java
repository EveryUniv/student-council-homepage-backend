package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.dto.RequestSignupDto;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final SMSVerificationService smsVerificationService;
    private final DKUAuthService dkuAuthService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public void signup(RequestSignupDto dto, String signupToken) {
        StudentInfo studentInfo = dkuAuthService.getDKUStudentInfo(signupToken);
        String phone = smsVerificationService.getPhoneNumber(signupToken);
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        User.UserBuilder userBuilder = User.builder()
                .studentId(studentInfo.getStudentId())
                .password(encryptedPassword)
                .major(studentInfo.getMajor())
                .name(studentInfo.getStudentName())
                .phone(phone)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER);

        if (studentInfo.getMajor() == Major.NO_DATA) {
            userBuilder.unexpectedMajorName(studentInfo.getNotRecognizedMajor());
        }

        userRepository.save(userBuilder.build());
    }
}
