package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.ExternalAPIPath;
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

    private final RequestToken tokenRequest = new RequestToken();

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
        this.tokenRequest.getAuth().setTenantId(tenantId);
        this.tokenRequest.getAuth().getPasswordCredentials().setUsername(username);
        this.tokenRequest.getAuth().getPasswordCredentials().setPassword(password);
    }

    public String requestToken() {
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