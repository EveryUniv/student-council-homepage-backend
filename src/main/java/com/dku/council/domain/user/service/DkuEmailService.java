package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.AlreadyStudentIdException;
import com.dku.council.domain.user.exception.MajorNotFoundException;
import com.dku.council.domain.user.exception.NotDKUAuthorizedException;
import com.dku.council.domain.user.exception.WrongEmailCodeException;
import com.dku.council.domain.user.model.DkuUserInfo;
import com.dku.council.domain.user.model.dto.request.RequestSendEmailCode;
import com.dku.council.domain.user.model.dto.request.RequestVerifyEmailCodeDto;
import com.dku.council.domain.user.model.dto.response.ResponseScrappedStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.domain.user.util.CodeGenerator;
import com.dku.council.global.util.TextTemplateEngine;
import com.dku.council.infra.nhn.service.NHNEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DkuEmailService {
    private static final String DKU_AUTH_NAME = "dku";
    private static final String EMAIL_AUTH_NAME = "email";

    private final Clock clock;
    private final NHNEmailService service;
    private final SignupAuthRepository dkuAuthRepository;
    private final MajorRepository majorRepository;
    private final UserRepository userRepository;

    @Value("${app.auth.email.code-length}")
    private final int codeLength;

    /**
     * 학번으로 이메일 인증 발송.
     *
     * @param dto 학번(8자리)
     */
    @Transactional(readOnly = true)
    public void sendEmailCode(RequestSendEmailCode dto) {
        String emailCode = CodeGenerator.generateHexCode(codeLength);
        String studentId = dto.getStudentId();
        Instant now = Instant.now(clock);

        checkAlreadyStudentId(studentId);
        dkuAuthRepository.setAuthPayload(studentId, EMAIL_AUTH_NAME, emailCode, now);

        String text = makeTemplatedEmail(
                dto.getStudentId(),
                emailCode
        );

        service.sendMessage(dto.getStudentId(), "단국대 학생 인증", text);
    }

    private void checkAlreadyStudentId(String studentId) {
        Optional<User> alreadyUser = userRepository.findByStudentId(studentId);
        if (alreadyUser.isPresent()) {
            throw new AlreadyStudentIdException();
        }
    }

    /**
     * 사용자 정보를 입력 받고, 입력받은 정보와 이메일 인증코드를 대조하여 일치할 경우
     * 사용자의 정보를 redis에 저장합니다.
     *
     * @param dto Major 를 String 으로 받습니다.
     */
    public ResponseVerifyStudentDto validateEmailCode(RequestVerifyEmailCodeDto dto) {
        String signupToken = UUID.randomUUID().toString();

        Instant now = Instant.now(clock);
        String emailCode = dkuAuthRepository.getAuthPayload(dto.getStudentId(), EMAIL_AUTH_NAME, String.class, now)
                .orElseThrow(NotDKUAuthorizedException::new);

        if (!emailCode.equalsIgnoreCase(dto.getEmailCode())) {
            throw new WrongEmailCodeException();
        }

        Major major = majorRepository.findById(dto.getMajorId())
                .orElseThrow(MajorNotFoundException::new);
        DkuUserInfo info = new DkuUserInfo(dto.getStudentName(), dto.getStudentId(), dto.getYearOfAdmission(),
                dto.getAcademicStatus(), major.getName(), major.getDepartment());

        dkuAuthRepository.setAuthPayload(signupToken, DKU_AUTH_NAME, info, now);

        ResponseScrappedStudentInfoDto studentInfoDto = new ResponseScrappedStudentInfoDto(info);
        return new ResponseVerifyStudentDto(signupToken, studentInfoDto);
    }

    /**
     * 저장되어 있는 학생 정보를 반환합니다.
     *
     * @param signupToken 학생인증 토큰
     * @return 학생정보
     */
    public DkuUserInfo getStudentInfo(String signupToken) throws NotDKUAuthorizedException {
        Instant now = Instant.now(clock);
        return dkuAuthRepository.getAuthPayload(signupToken, DKU_AUTH_NAME, DkuUserInfo.class, now)
                .orElseThrow(NotDKUAuthorizedException::new);
    }


    private String makeTemplatedEmail(String studentId, String buttonContent) {
        return new TextTemplateEngine.Builder()
                .argument("studentId", studentId)
                .argument("emailContent", "단국대학교 재학생 인증을 위해, 아래의 코드를\n입력해 주세요.")
                .argument("linkButtonContent", buttonContent)
                .build()
                .readHtmlFromResource("auth_email_content.html");
    }


}

