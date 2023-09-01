package com.dku.council.infra.nhn.service;

import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.nhn.exception.CannotSendMMSException;
import com.dku.council.infra.nhn.model.dto.request.RequestNHNCloudMMS;
import com.dku.council.infra.nhn.model.dto.request.RequestNHNCloudSMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudMMS;
import com.dku.council.infra.nhn.model.dto.response.ResponseNHNCloudMMS.Body.Data.SendResult;
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
public class MMSService {

    private final WebClient webClient;

    @Value("${nhn.mms.api-path}")
    private final String nhnApiPath;

    @Value("${nhn.mms.secret-key}")
    private final String secretKey;

    @Value("${nhn.mms.sender-phone}")
    private final String senderPhone;

    /**
     * MMS를 전송합니다.
     *
     * @param title 제목
     * @param phone 전화번호
     * @param body  전송 내용
     */
    public void sendMMS(String title, String phone, String body) {

        RequestNHNCloudMMS request = new RequestNHNCloudMMS(title, senderPhone, phone, body);

        ResponseNHNCloudMMS response = null;
        try{
            response = webClient.post()
                    .uri(nhnApiPath)
                    .header("X-Secret-Key", secretKey)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), RequestNHNCloudSMS.class)
                    .retrieve()
                    .bodyToMono(ResponseNHNCloudMMS.class)
                    .block();

            validateResponse(response);
        } catch(WebClientResponseException e){
            throw new CannotSendMMSException(e);
        } catch(Throwable e){
            if(response != null){
                log.debug(String.format("Error while sending MMS to %s: %s", phone, response));
                log.debug(response.toString());
            }
            throw new CannotSendMMSException(e);
        }
    }

    private static void validateResponse(ResponseNHNCloudMMS response) {
        if (response == null || response.getHeader() == null || response.getBody() == null) {
            throw new UnexpectedResponseException("response is incorrect");
        } else {
            ResponseNHNCloudMMS.Header header = response.getHeader();
            if (!header.getIsSuccessful()) {
                throw new UnexpectedResponseException(header.getResultMessage());
            }

            List<SendResult> results = Optional.of(response)
                    .map(ResponseNHNCloudMMS::getBody)
                    .map(ResponseNHNCloudMMS.Body::getData)
                    .map(ResponseNHNCloudMMS.Body.Data::getSendResultList)
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