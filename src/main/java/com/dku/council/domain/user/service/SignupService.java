package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.AlreadyNicknameException;
import com.dku.council.domain.user.exception.AlreadyStudentIdException;
import com.dku.council.domain.user.exception.IllegalNicknameException;
import com.dku.council.domain.user.model.DkuUserInfo;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.dto.request.RequestSignupDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.NicknameFilterRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.role.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final SMSVerificationService smsVerificationService;
    private final DKUAuthService dkuAuthService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final NicknameFilterRepository nicknameFilterRepository;
    private final UserInfoService userInfoService;

    @Transactional
    public void signup(RequestSignupDto dto, String signupToken) {
        DkuUserInfo studentInfo = dkuAuthService.getStudentInfo(signupToken);

        checkAlreadyStudentId(studentInfo.getStudentId());
        checkNickname(dto.getNickname());

        String phone = smsVerificationService.getPhoneNumber(signupToken);
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        Major major = retrieveMajor(studentInfo.getMajorName(), studentInfo.getDepartmentName());

        Optional<User> inactiveUser = userRepository.findByInactiveStudentId(studentInfo.getStudentId());

        if (inactiveUser.isPresent()) {
            User user = inactiveUser.get();
            user.changeStatus(UserStatus.ACTIVE);
            user.changeNickName(dto.getNickname());
            user.changePhone(phone);
            user.changePassword(encryptedPassword);
            user.changeGenericInfo(studentInfo.getStudentId(), studentInfo.getStudentName(), major,
                    studentInfo.getYearOfAdmission(), studentInfo.getStudentState());
            userInfoService.invalidateUserInfo(user.getId());
        } else {
            User user = User.builder()
                    .studentId(studentInfo.getStudentId())
                    .password(encryptedPassword)
                    .name(studentInfo.getStudentName())
                    .nickname(dto.getNickname())
                    .phone(phone)
                    .major(major)
                    .yearOfAdmission(studentInfo.getYearOfAdmission())
                    .academicStatus(studentInfo.getStudentState())
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        deleteSignupAuths(signupToken);
    }

    public void checkNickname(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new AlreadyNicknameException();
        }

        if (nicknameFilterRepository.countMatchedFilter(nickname) > 0) {
            throw new IllegalNicknameException();
        }
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

    private void checkAlreadyStudentId(String studentId) {
        Optional<User> alreadyUser = userRepository.findByStudentId(studentId);
        if (alreadyUser.isPresent()) {
            throw new AlreadyStudentIdException();
        }
    }
}
