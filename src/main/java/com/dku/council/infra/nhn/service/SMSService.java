package com.dku.council.infra.nhn.service;

import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.infra.nhn.model.dto.request.RequestNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS.Body.Data.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSService {

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

        ResponseNHNCloudSMS response = null;
        try {
            response = webClient.post()
                    .uri(apiPath)
                    .header("X-Secret-Key", secretKey)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), RequestNHNCloudSMS.class)
                    .retrieve()
                    .bodyToMono(ResponseNHNCloudSMS.class)
                    .block();

            validateResponse(response);
        } catch (WebClientResponseException e) {
            throw new CannotSendSMSException(e);
        } catch (Throwable e) {
            if (response != null) {
                log.debug(String.format("Error while sending SMS to %s: %s", phone, response));
                log.debug(response.toString());
            }
            throw new CannotSendSMSException(e);
        }
    }

    private static void validateResponse(ResponseNHNCloudSMS response) {
        if (response == null || response.getHeader() == null || response.getBody() == null) {
            throw new UnexpectedResponseException("response is incorrect");
        } else {
            ResponseNHNCloudSMS.Header header = response.getHeader();
            if (!header.getIsSuccessful()) {
                throw new UnexpectedResponseException(header.getResultMessage());
            }

            List<SendResult> results = Optional.of(response)
                    .map(ResponseNHNCloudSMS::getBody)
                    .map(ResponseNHNCloudSMS.Body::getData)
                    .map(ResponseNHNCloudSMS.Body.Data::getSendResultList)
                    .orElse(null);

            if (results == null) {
                throw new UnexpectedResponseException("Invalid body");
            } else {
                for (SendResult result : results) {
                    if (result.getResultCode() != 0) {
                        throw new UnexpectedResponseException(result.getResultMessage());
                    }
                }
            }
        }
    }
}
