package com.dku.council.infra.dku.scrapper;

import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.YearMonth;

@RequiredArgsConstructor
class DkuScrapper {

    private final WebClient webClient;

    protected String requestWebInfo(DkuAuth auth, String uri) {
        String result;
        try {
            result = makeRequestWebInfo(auth, uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        if (result == null) {
            throw new DkuFailedCrawlingException("Failed to crawl");
        }

        return result;
    }

    protected <T> T requestPortal(DkuAuth auth, String uri, Class<T> clazz) {
        T result;
        try {
            result = makeRequest(auth, uri, "https://portal.dankook.ac.kr/p/S01/")
                    .retrieve()
                    .bodyToMono(clazz)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        if (result == null) {
            throw new DkuFailedCrawlingException("Failed to crawl");
        }

        return result;
    }

    protected WebClient.RequestBodySpec makeRequestWebInfo(DkuAuth auth, String uri) {
        return makeRequest(auth, uri, "https://webinfo.dankook.ac.kr/");
    }

    private WebClient.RequestBodySpec makeRequest(DkuAuth auth, String uri, String referer) {
        return webClient.post()
                .uri(uri)
                .cookies(auth.authCookies())
                .header("Referer", referer);
    }

    public static String getSemester(YearMonth yearMonth) {
        int month = yearMonth.getMonthValue();
        if (month >= 2 && month <= 8) {
            return "1";
        } else {
            return "2";
        }
    }
}
