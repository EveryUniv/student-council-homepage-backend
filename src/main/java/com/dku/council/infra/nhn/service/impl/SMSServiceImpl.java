package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.ExternalAPIPath;
import com.dku.council.infra.nhn.exception.CannotSendSMSException;
import com.dku.council.infra.nhn.model.dto.request.RequestNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudSMS;
import com.dku.council.infra.nhn.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {

    private final RestTemplate restTemplate;

    @Value("${nhn.sms.app-key}")
    private final String appKey;

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
        final HttpHeaders headers = new HttpHeaders(); // 헤더에 key들을 담아준다.
        headers.set("X-Secret-Key", secretKey);
        headers.set("Content-Type", "application/json");

        // request api
        // TODO RestTemplate대신 WebClient로 교체하기.
        RequestNHNCloudSMS request = new RequestNHNCloudSMS(senderPhone, phone, body);
        HttpEntity<RequestNHNCloudSMS> entity = new HttpEntity<>(request, headers);
        String url = ExternalAPIPath.NHNCloudSMS(appKey);
        ResponseNHNCloudSMS response = restTemplate.postForObject(url, entity, ResponseNHNCloudSMS.class);

        log.info(String.format("Result of sending SMS to %s: %s", phone, response));

        // handle response
        String failReason = null;
        if (response == null || response.getHeader() == null) {
            failReason = "response or header is null";
        } else {
            ResponseNHNCloudSMS.Header header = response.getHeader();
            if (!header.getIsSuccessful()) {
                failReason = header.getResultMessage();
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
