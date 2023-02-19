package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.ExternalAPIPath;
import com.dku.council.infra.nhn.exception.NotInitializedException;
import com.dku.council.infra.nhn.model.dto.request.RequestToken;
import com.dku.council.infra.nhn.model.dto.response.ResponseToken;
import com.dku.council.infra.nhn.service.NHNAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class NHNAuthServiceImpl implements NHNAuthService {

    private RequestToken tokenRequest;

    // TODO RestTemplate대신 WebClient로 교체하기.
    private final RestTemplate restTemplate;

    @Value("${nhn.os.tenantId}")
    private final String tenantId;

    @Value("${nhn.os.username}")
    private final String username;

    @Value("${nhn.os.password}")
    private final String password;

    @PostConstruct
    private void initialize() {
        RequestToken.PasswordCredentials pwdCred = new RequestToken.PasswordCredentials(username, password);
        RequestToken.Auth auth = new RequestToken.Auth(tenantId, pwdCred);
        tokenRequest = new RequestToken(auth);
    }

    public String requestToken() {
        if (tokenRequest == null) {
            throw new NotInitializedException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<RequestToken> httpEntity = new HttpEntity<>(this.tokenRequest, headers);
        ResponseEntity<ResponseToken> response = this.restTemplate.exchange(
                ExternalAPIPath.NHNAuth, HttpMethod.POST, httpEntity, ResponseToken.class);
        ResponseToken token = response.getBody();

        if (token == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return token.getTokenId();
    }

}