package com.dku.council.util;

import io.netty.handler.logging.LogLevel;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class WebClientUtil {
    public static ClientHttpConnector logger() {
        HttpClient httpClient = HttpClient.create()
                .wiretap(WebClientUtil.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        return new ReactorClientHttpConnector(httpClient);
    }
}
