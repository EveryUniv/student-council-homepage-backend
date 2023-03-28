package com.dku.council.infra.dku.scrapper;

import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
class DkuScrapper {

    private final WebClient webClient;

    protected String requestWebInfo(DkuAuth auth, String uri) {
        String result;
        try {
            result = webClient.post()
                    .uri(uri)
                    .cookies(auth.authCookies())
                    .header("Referer", "https://webinfo.dankook.ac.kr/")
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
            result = webClient.post()
                    .uri(uri)
                    .cookies(auth.authCookies())
                    .header("Referer", "https://portal.dankook.ac.kr/p/S01/")
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
}
