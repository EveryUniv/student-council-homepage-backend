package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.model.dto.NhnMessage;
import com.dku.council.infra.nhn.service.NHNEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NHNEmailServiceImpl implements NHNEmailService {
    private final WebClient client;
    private final ObjectMapper objectMapper;
    @Value("${email.api-path}")
    private final String NHN_EMAIL_URL;
    @Value("${email.secret-key}")
    private final String NHN_EMAIL_KEY;
    @Value("${email.sender-mail}")
    private final String NHN_SENDER;
    @Value("${email.sender-name}")
    private final String NHN_SENDER_NAME;

    @Override
    public void sendMessage(String studentId, String title, String text) {

        ResponseEntity<String> response = client.post()
                .uri(NHN_EMAIL_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-Secret-Key", NHN_EMAIL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(makeMessage(title, studentId, text))
                .retrieve()
                .toEntity(String.class)
                .block();
        if(response == null || response.getStatusCode().isError()){
            throw new RuntimeException("전송 실패");
        }

        log.info("message = {}", response.getBody());
    }

    private NhnMessage makeMessage(String studentId, String title, String text){
        return NhnMessage.builder()
                .senderAddress(NHN_SENDER)
                .senderName(NHN_SENDER_NAME)
                .title(title)
                .body(text)
                .receiver(NhnMessage.Receiver.builder()
                        .receiveMailAddr(studentId + "@dankook.ac.kr")
                        .build())
                .build();
    }

}
