package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.AlreadyStudentIdException;
import com.dku.council.domain.user.exception.NotDKUAuthorizedException;
import com.dku.council.domain.user.model.DkuUserInfo;
import com.dku.council.domain.user.model.dto.request.RequestDkuStudentDto;
import com.dku.council.domain.user.model.dto.response.ResponseScrappedStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.domain.user.util.CodeGenerator;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentInfo;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DKUAuthService {

    public static final String DKU_AUTH_NAME = "dku";

    private final Clock clock;
    private final DkuStudentService crawlerService;
    private final DkuAuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserInfoService userInfoService;
    private final SignupAuthRepository dkuAuthRepository;
    private final MajorRepository majorRepository;

    /**
     * 회원가입 토큰을 기반으로 인증된 학생 정보를 가져옵니다. 학생인증이 되어있지 않으면 Exception이 발생합니다.
     * 이 메서드는 회원가입 진행자를 대상으로 합니다.
     *
     * @param signupToken 회원가입 토큰
     * @throws NotDKUAuthorizedException 학생 인증을 하지 않았을 경우
     */
    public DkuUserInfo getStudentInfo(String signupToken) throws NotDKUAuthorizedException {
        Instant now = Instant.now(clock);
        return dkuAuthRepository.getAuthPayload(signupToken, DKU_AUTH_NAME, DkuUserInfo.class, now)
                .orElseThrow(NotDKUAuthorizedException::new);
    }

    /**
     * 회원가입 토큰을 기반으로 인증된 학생 정보를 삭제합니다. 회원가입이 끝나면
     * 학생 인증 정보를 모두 삭제해야합니다.
     *
     * @param signupToken 회원가입 토큰
     */
    public boolean deleteStudentAuth(String signupToken) {
        return dkuAuthRepository.deleteAuthPayload(signupToken, DKU_AUTH_NAME);
    }

    /**
     * 단대 id, password로 학생 인증을 진행합니다.
     *
     * @param dto 요청 dto
     * @return 학생 인증 결과 dto
     */
    @Transactional(readOnly = true)
    public ResponseVerifyStudentDto verifyStudent(RequestDkuStudentDto dto) {
        String signupToken = CodeGenerator.generateUUIDCode();
        checkAlreadyStudentId(dto);

        DkuUserInfo info = retrieveDkuUserInfo(dto.getDkuStudentId(), dto.getDkuPassword());
        dkuAuthRepository.setAuthPayload(signupToken, DKU_AUTH_NAME, info, Instant.now(clock));

        ResponseScrappedStudentInfoDto studentInfoDto = new ResponseScrappedStudentInfoDto(info);
        return new ResponseVerifyStudentDto(signupToken, studentInfoDto);
    }

    private void checkAlreadyStudentId(RequestDkuStudentDto dto) {
        Optional<User> alreadyUser = userRepository.findByStudentId(dto.getDkuStudentId());
        if (alreadyUser.isPresent()) {
            throw new AlreadyStudentIdException();
        }
    }

    /**
     * 단대 id, password로 학생 정보를 가져와 계정에 업데이트합니다.
     *
     * @param dto 요청 dto
     * @return 학생 인증 결과 dto
     */
    @Transactional(readOnly = true)
    public ResponseScrappedStudentInfoDto updateDKUStudent(Long userId, RequestDkuStudentDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        DkuUserInfo info = retrieveDkuUserInfo(dto.getDkuStudentId(), dto.getDkuPassword());

        Major major = retrieveMajor(info.getMajorName(), info.getDepartmentName());

        user.changeGenericInfo(
                info.getStudentId(),
                info.getStudentName(),
                major,
                info.getYearOfAdmission(),
                info.getStudentState());

        userInfoService.invalidateUserInfo(userId);
        return new ResponseScrappedStudentInfoDto(info);
    }

    private DkuUserInfo retrieveDkuUserInfo(String id, String pwd) {
        DkuAuth auth = authenticationService.loginWebInfo(id, pwd);
        StudentInfo studentInfo = crawlerService.crawlStudentInfo(auth);
        return new DkuUserInfo(studentInfo);
    }

    private Major retrieveMajor(String majorName, String departmentName) {
        return majorRepository.findByName(majorName, departmentName)
                .orElseGet(() -> {
                    Major entity = new Major(majorName, departmentName);
                    entity = majorRepository.save(entity);
                    return entity;
                });
    }
}
