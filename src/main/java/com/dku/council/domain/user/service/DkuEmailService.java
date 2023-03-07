package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.NotDKUAuthorizedException;
import com.dku.council.domain.user.exception.NotSMSSentException;
import com.dku.council.domain.user.exception.WrongEmailCodeException;
import com.dku.council.domain.user.exception.WrongSMSCodeException;
import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.SMSAuth;
import com.dku.council.domain.user.model.dto.request.RequestSendEmailCode;
import com.dku.council.domain.user.model.dto.request.RequestVerifyEmailCodeDto;
import com.dku.council.domain.user.model.dto.response.ResponseStudentInfoDto;
import com.dku.council.domain.user.model.dto.response.ResponseVerifyStudentDto;
import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.global.util.TextTemplateEngine;
import com.dku.council.infra.dku.model.StudentInfo;
import com.dku.council.infra.nhn.service.NHNEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Objects;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DkuEmailService {
    private static final String DKU_AUTH_NAME = "dku";
    private static final String EMAIL_AUTH_NAME = "email";
    private final NHNEmailService service;
    private final SignupAuthRepository dkuAuthRepository;
    private final MessageSource messageSource;


    /**
     * 학번으로 이메일 인증 발송.
     * @param dto 학번(8자리)
     */
    public void sendEmailCode(RequestSendEmailCode dto){
        String emailCode = UUID.randomUUID().toString().substring(0, 5);
        String studentId = dto.getStudentId();

        dkuAuthRepository.setAuthPayload(studentId, EMAIL_AUTH_NAME, emailCode);

        String text = makeTemplatedEmail(
                dto.getStudentId(),
                emailCode
        );

        service.sendMessage(dto.getStudentId(), "단국대 학생회인증", text);
    }

    /**
     * 사용자 정보를 입력 받고, 입력받은 정보와 이메일 인증코드를 대조하여 일치할 경우
     * 사용자의 정보를 redis에 저장합니다.
     * @param dto Major 를 String 으로 받습니다.
     * @return
     */
    public ResponseVerifyStudentDto validateEmailCode(RequestVerifyEmailCodeDto dto) {
        String signupToken = UUID.randomUUID().toString();

        String emailCode = dkuAuthRepository.getAuthPayload(dto.getStudentId(), EMAIL_AUTH_NAME, String.class)
                .orElseThrow(NotDKUAuthorizedException::new);

        if(!Objects.equals(emailCode, dto.getEmailCode())){
            throw new WrongEmailCodeException();
        }

        StudentInfo studentInfo = new StudentInfo(dto.getStudentName(), dto.getStudentId(), dto.getYearOfAdmission(), MajorData.of(messageSource, dto.getMajorData()));

        dkuAuthRepository.setAuthPayload(signupToken, DKU_AUTH_NAME, studentInfo);

        ResponseStudentInfoDto studentInfoDto = ResponseStudentInfoDto.from(messageSource, studentInfo);
        return new ResponseVerifyStudentDto(signupToken, studentInfoDto);
    }

    /**
     * 저장되어 있는 학생 정보를 반환합니다.
     * @param signupToken 학생인증 토큰
     * @return 학생정보
     * @throws NotDKUAuthorizedException
     */
    public StudentInfo getStudentInfo(String signupToken) throws NotDKUAuthorizedException {
        return dkuAuthRepository.getAuthPayload(signupToken, DKU_AUTH_NAME, StudentInfo.class)
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

