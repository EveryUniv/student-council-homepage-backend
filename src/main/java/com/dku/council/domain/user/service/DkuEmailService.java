package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.dto.request.RequestSendEmailCode;
import com.dku.council.domain.user.model.dto.request.RequestVerifyEmailCodeDto;
import com.dku.council.domain.user.repository.SignupAuthRepository;
import com.dku.council.global.util.TextTemplateEngine;
import com.dku.council.infra.nhn.model.dto.NhnMessage;
import com.dku.council.infra.nhn.service.NHNEmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DkuEmailService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String EMAIL_AUTH_NAME = "email";

    private final NHNEmailService service;
    private final SignupAuthRepository emailAuthRepository;

    public void sendEmailCode(RequestSendEmailCode dto) throws JsonProcessingException {
        String emailCode = UUID.randomUUID().toString().substring(0, 5);
        String studentId = dto.getStudentId();

        emailAuthRepository.setAuthPayload(emailCode, EMAIL_AUTH_NAME, studentId);

        String text = makeTemplatedEmail(
                dto.getStudentId(),
                emailCode
        );

        service.sendMessage(dto.getStudentId(), "단국대 학생회인증", text);
    }

    public String validateEmailCode(RequestVerifyEmailCodeDto dto) {

        String studentId = emailAuthRepository.getAuthPayload(dto.getEmailCode(), EMAIL_AUTH_NAME, String.class)
                .orElseThrow(RuntimeException::new);

        return studentId;
    }


    private String makeTemplatedEmail(String studentId, String buttonContent) {
        return new TextTemplateEngine.Builder()
                .argument("studentId", studentId)
                .argument("emailContent", "단국대학교 재학생 인증을 위해, 아래의 코드를\n입력해 주세요.")
                .argument("linkButtonContent", buttonContent)
                .build()
                .readHtmlFromResource("auth_email_content_mobile.html");
    }


}

