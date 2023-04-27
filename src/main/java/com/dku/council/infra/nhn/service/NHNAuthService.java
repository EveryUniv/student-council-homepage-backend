package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.CannotGetTokenException;
import com.dku.council.infra.nhn.exception.NotInitializedException;
import com.dku.council.infra.nhn.model.dto.request.RequestToken;
import com.dku.council.infra.nhn.model.dto.response.ResponseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NHNAuthService {

    private RequestToken tokenRequest;

    private final WebClient webClient;

    @Value("${nhn.auth.api-path}")
    private final String apiPath;

    @Value("${nhn.auth.tenant-id}")
    private final String tenantId;

    @Value("${nhn.auth.username}")
    private final String username;

    @Value("${nhn.auth.password}")
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

        ResponseToken response;
        try {
            response = webClient.post()
                    .uri(apiPath)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(tokenRequest), RequestToken.class)
                    .retrieve()
                    .bodyToMono(ResponseToken.class)
                    .block();
        } catch (Throwable e) {
            throw new CannotGetTokenException(e);
        }

        String token = Optional.ofNullable(response)
                .map(ResponseToken::getAccess)
                .map(ResponseToken.Access::getToken)
                .map(ResponseToken.Token::getId)
                .orElse(null);

        if (token == null) {
            throw new CannotGetTokenException();
        }

        return token;
    }

}