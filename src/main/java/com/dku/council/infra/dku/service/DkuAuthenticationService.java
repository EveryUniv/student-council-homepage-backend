package com.dku.council.infra.dku.service;

import com.dku.council.global.config.qualifier.ChromeAgentWebClient;
import com.dku.council.infra.dku.exception.DkuFailedLoginException;
import com.dku.council.infra.dku.model.DkuAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO 접속이 너무 몰리면 약간 지연하도록
// TODO 한번에 로그인 너무 많이 요청하면 blocking 당할지도?
// TODO 현재 동기식이지만, 비동기식으로 바꿔보기
@Service
@RequiredArgsConstructor
public class DkuAuthenticationService {

    private static final Pattern ERROR_ALERT_PATTERN = Pattern.compile("\\);[\\s\\n]+alert\\(\"(.+)\"\\)");

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${dku.login.api-path}")
    private final String loginApiPath;

    /**
     * 단국대학교 웹정보 시스템에 로그인합니다.
     *
     * @param classId  아이디 (학번)
     * @param password 비밀번호
     * @return 토큰 쿠키가 포함된 DkuAuth
     */
    public DkuAuth login(String classId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        ClientResponse response = tryLogin(classId, password);
        ClientResponse.Headers headers = response.headers();
        addMappedCookies(cookies, response.cookies());

        List<String> ssoLocations = headers.header("Location");
        if (ssoLocations.size() != 1) {
            throw new DkuFailedLoginException();
        }

        String ssoLocation = ssoLocations.get(0);
        response = trySSOAuth(ssoLocation, cookies);
        addMappedCookies(cookies, response.cookies());

        return new DkuAuth(cookies);
    }

    private void addMappedCookies(MultiValueMap<String, String> dest, MultiValueMap<String, ResponseCookie> src) {
        Set<Map.Entry<String, List<ResponseCookie>>> cookies = src.entrySet();
        for (Map.Entry<String, List<ResponseCookie>> ent : cookies) {
            for (ResponseCookie value : ent.getValue()) {
                dest.add(ent.getKey(), value.getValue());
            }
        }
    }

    private ClientResponse tryLogin(String classId, String password) {
        String param = String.format("username=%s&password=%s&tabIndex=0", classId, password);

        ClientResponse response;
        try {
            response = webClient.post()
                    .uri(loginApiPath)
                    .header("Origin", "https://webinfo.dankook.ac.kr")
                    .header("Referer", "https://webinfo.dankook.ac.kr/member/logon.do?returnurl=http://webinfo.dankook.ac.kr:80/main.do&sso=ok")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .exchangeToMono(Mono::just)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedLoginException(t);
        }

        validateResponse(response);

        HttpStatus statusCode = response.statusCode();
        if (statusCode == HttpStatus.OK) {
            throwLoginFailedWithAlertMessage(response);
        }

        validateStatusCode(statusCode);
        return response;
    }

    private ClientResponse trySSOAuth(String ssoUrl, MultiValueMap<String, String> cookies) {
        ClientResponse response;

        try {
            response = webClient.post()
                    .uri(ssoUrl)
                    .cookies(map -> map.addAll(cookies))
                    .header("Referer", "https://webinfo.dankook.ac.kr/")
                    .exchangeToMono(Mono::just)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedLoginException(t);
        }

        validateResponse(response);
        validateStatusCode(response.statusCode());
        return response;
    }

    // TODO 제대로 동작 안함. body가 정확하게 출력되도록 수정
    private static void throwLoginFailedWithAlertMessage(ClientResponse response) {
        String responseBody = response.bodyToMono(String.class).block();
        if (responseBody == null) {
            throw new DkuFailedLoginException();
        }

        String loginMessage = extractLoginMessage(responseBody);
        if (loginMessage != null) {
            throw new DkuFailedLoginException(loginMessage);
        } else {
            throw new DkuFailedLoginException();
        }
    }

    private static String extractLoginMessage(String html) {
        Matcher matcher = ERROR_ALERT_PATTERN.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static void validateResponse(ClientResponse response) {
        if (response == null) {
            throw new DkuFailedLoginException(new NullPointerException("response"));
        }
    }

    private static void validateStatusCode(HttpStatus statusCode) {
        if (statusCode != HttpStatus.FOUND) {
            throw new DkuFailedLoginException(new ResponseStatusException(statusCode));
        }
    }
}
