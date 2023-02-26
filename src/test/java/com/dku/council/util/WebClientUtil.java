package com.dku.council.util;

import io.netty.handler.logging.LogLevel;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class WebClientUtil {

    /**
     * 테스트용으로 적합한 로깅용 Client Connector를 만듭니다. 이걸 WebClient생성할 때 builder에 clientConnector로
     * 넘겨주면 모든 Request와 Response를 보여줍니다.
     * @return logging connector 반환
     */
    public static ClientHttpConnector logger() {
        HttpClient httpClient = HttpClient.create()
                .wiretap(WebClientUtil.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        return new ReactorClientHttpConnector(httpClient);
    }
}
