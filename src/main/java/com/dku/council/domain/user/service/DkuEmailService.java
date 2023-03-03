package com.dku.council.domain.user.service;

import com.dku.council.domain.user.exception.WrongSMSCodeException;
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


    public void sendEmailCode(RequestSendEmailCode dto){
        String emailCode = UUID.randomUUID().toString().substring(0, 5);
        String studentId = dto.getStudentId();

        dkuAuthRepository.setAuthPayload(emailCode, EMAIL_AUTH_NAME, studentId);

        String text = makeTemplatedEmail(
                dto.getStudentId(),
                emailCode
        );

        service.sendMessage(dto.getStudentId(), "단국대 학생회인증", text);
    }

    public ResponseVerifyStudentDto validateEmailCode(RequestVerifyEmailCodeDto dto) {
        String signupToken = UUID.randomUUID().toString();

        String studentId = dkuAuthRepository.getAuthPayload(dto.getEmailCode(), EMAIL_AUTH_NAME, String.class)
                .orElseThrow(RuntimeException::new);

        if(!Objects.equals(studentId, dto.getStudentId())) throw new WrongSMSCodeException();

        StudentInfo studentInfo = new StudentInfo(dto.getStudentName(), studentId, dto.getYearOfAdmission(), dto.getMajorData());

        dkuAuthRepository.setAuthPayload(signupToken, DKU_AUTH_NAME, studentInfo);

        ResponseStudentInfoDto studentInfoDto = ResponseStudentInfoDto.from(messageSource, studentInfo);
        return new ResponseVerifyStudentDto(signupToken, studentInfoDto);
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

