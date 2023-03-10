package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.AlreadyStudentIdException;
import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.dto.request.RequestSignupDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final SMSVerificationService smsVerificationService;
    private final DKUAuthService dkuAuthService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void signup(RequestSignupDto dto, String signupToken) {
        StudentInfo studentInfo = dkuAuthService.getStudentInfo(signupToken);
        String phone = smsVerificationService.getPhoneNumber(signupToken);
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        checkAlreadyStudentId(studentInfo.getStudentId());

        User.UserBuilder userBuilder = User.builder()
                .studentId(studentInfo.getStudentId())
                .password(encryptedPassword)
                .name(studentInfo.getStudentName())
                .phone(phone)
                .yearOfAdmission(studentInfo.getYearOfAdmission())
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER);

        MajorData majorData = studentInfo.getMajorData();
        if (majorData.isEmpty()) {
            userBuilder.major(new Major(studentInfo.getNotRecognizedMajor(), studentInfo.getNotRecognizedDepartment()));
        } else {
            userBuilder.major(new Major(majorData));
        }

        userRepository.save(userBuilder.build());
        deleteSignupAuths(signupToken);
    }

    private void deleteSignupAuths(String signupToken) {
        if (!dkuAuthService.deleteStudentAuth(signupToken)) {
            log.error("Can't delete user signup authentication: dku student auth");
        }
        if (!smsVerificationService.deleteSMSAuth(signupToken)) {
            log.error("Can't delete user signup authentication: sms auth");
        }
    }

    private void checkAlreadyStudentId(String studentId) {
        Optional<User> alreadyUser = userRepository.findByStudentId(studentId);
        if (alreadyUser.isPresent()) {
            throw new AlreadyStudentIdException();
        }
    }
}
