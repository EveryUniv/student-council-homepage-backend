package com.dku.council.infra.naver.service;

import com.dku.council.infra.naver.component.CaptchaApiPath;
import com.dku.council.infra.naver.exception.CannotRequestCaptchaException;
import com.dku.council.infra.naver.exception.InvalidCaptchaException;
import com.dku.council.infra.naver.exception.InvalidCaptchaKeyException;
import com.dku.council.infra.naver.exception.UnissuedCaptchaException;
import com.dku.council.infra.naver.model.response.ResponseCaptchaKey;
import com.dku.council.infra.naver.model.response.ResponseCaptchaValidation;
import com.dku.council.infra.naver.model.response.ResponseError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    @Value("${naver.captcha.enable}")
    private final boolean enableCaptcha;

    private final ObjectMapper objectMapper;
    private final CaptchaApiPath captchaApiPath;
    private final WebClient webClient;


    public String requestCaptchaKey() {
        try {
            String response = captchaApiPath.requestKey(webClient.get())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new NullPointerException("Captcha key response is null");
            }

            ResponseCaptchaKey responseKey = objectMapper.readValue(response, ResponseCaptchaKey.class);
            return responseKey.getKey();
        } catch (Throwable e) {
            throw new CannotRequestCaptchaException(e);
        }
    }

    public byte[] requestCaptchaImage(String key) {
        try {
            byte[] response = captchaApiPath.requestImage(webClient.get(), key)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if (response == null) {
                throw new NullPointerException("Captcha image response is null");
            }
            return response;
        } catch (WebClientResponseException e) {
            handleResponseError(e);
            throw new CannotRequestCaptchaException(e);
        } catch (Throwable e) {
            throw new CannotRequestCaptchaException(e);
        }
    }

    public void verifyCaptcha(String captchaKey, String captchaValue) {
        if (!enableCaptcha) {
            return;
        }

        try {
            String response = captchaApiPath.requestValidation(
                            webClient.get(), captchaKey, captchaValue)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new NullPointerException("Captcha image response is null");
            }

            ResponseCaptchaValidation responseObject = objectMapper.readValue(response, ResponseCaptchaValidation.class);
            if (!responseObject.getResult()) {
                throw new InvalidCaptchaException();
            }
        } catch (WebClientResponseException e) {
            handleResponseError(e);
            throw new CannotRequestCaptchaException(e);
        } catch (InvalidCaptchaException e) {
            throw e;
        } catch (Throwable e) {
            throw new CannotRequestCaptchaException(e);
        }
    }

    private void handleResponseError(WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        try {
            ResponseError error = objectMapper.readValue(body, ResponseError.class);
            switch (error.getErrorCode()) {
                case "CT001":
                    throw new InvalidCaptchaKeyException();
                case "CT002":
                    throw new UnissuedCaptchaException();
            }
        } catch (JsonProcessingException ignored) {
        }
    }
}
