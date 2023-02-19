package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.infra.nhn.model.dto.request.RequestNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS.Body.Data.SendResult;
import com.dku.council.infra.nhn.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO Test it
@Slf4j
@Service
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {

    private final WebClient webClient;

    @Value("${nhn.sms.api-path}")
    private final String apiPath;

    @Value("${nhn.sms.secret-key}")
    private final String secretKey;

    @Value("${nhn.sms.sender-phone}")
    private final String senderPhone;

    /**
     * SMS를 전송합니다.
     *
     * @param phone 전화번호
     * @param body  전송 내용
     */
    public void sendSMS(String phone, String body) {
        RequestNHNCloudSMS request = new RequestNHNCloudSMS(senderPhone, phone, body);

        ResponseNHNCloudSMS response = webClient.post()
                .uri(apiPath)
                .header("X-Secret-Key", secretKey)
                .header("Content-Type", "application/json")
                .body(Mono.just(request), RequestNHNCloudSMS.class)
                .retrieve()
                .bodyToMono(ResponseNHNCloudSMS.class)
                .block();

        log.info(String.format("Result of sending SMS to %s: %s", phone, response));

        // handle response
        String failReason = null;
        if (response == null || response.getHeader() == null || response.getBody() == null) {
            failReason = "response is incorrect";
        } else {
            ResponseNHNCloudSMS.Header header = response.getHeader();
            if (!header.getIsSuccessful()) {
                failReason = header.getResultMessage();
            }

            List<SendResult> results = response.getBody().getData().getSendResultList();
            for (SendResult result : results) {
                if (result.getResultCode() != 0) {
                    failReason = result.getResultMessage();
                    break;
                }
            }
        }

        if (failReason != null) {
            log.error("Can't send sms message: {}", failReason);
            if (response != null) {
                log.debug(response.toString());
            }
            throw new CannotSendSMSException();
        }
    }
}
