package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.exception.CannotGetBusArrivalException;
import com.dku.council.domain.bus.model.BusArrival;
import com.dku.council.domain.bus.model.dto.ResponseOpenApiBusArrival;
import com.dku.council.global.error.exception.UnexpectedResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenApiBusService {

    private final WebClient webClient;

    @Value("${bus.api-path}")
    private final String apiPath;

    @Value("${bus.key}")
    private final String serviceKey;

    /**
     * 버스 도착 정보를 OpenAPI를 통해 가져옵니다.
     *
     * @param stationId 정류소 ID
     * @return 버스 도착정보 목록 (도착정보가 없는 버스는 List에 포함되지 않음)
     */
    public List<BusArrival> retrieveBusArrival(String stationId) {
        ResponseOpenApiBusArrival response;
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(apiPath)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("stationId", stationId)
                    .build()
                    .toUri();

            // TODO 비동기 방식으로 처리해보기
            response = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(ResponseOpenApiBusArrival.class)
                    .block();

            if (response == null) {
                throw new UnexpectedResponseException("Failed response");
            }

            ResponseOpenApiBusArrival.Header header = response.getMsgHeader();
            if (header.getResultCode() != 0) {
                throw new UnexpectedResponseException(header.getResultMessage());
            }

            return response.getMsgBody().getBusArrivalList().stream()
                    .map(BusArrival::new)
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            throw new CannotGetBusArrivalException(e);
        }
    }
}
