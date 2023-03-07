package com.dku.council.infra.bus.service.api;

import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.bus.exception.CannotGetBusArrivalException;
import com.dku.council.infra.bus.exception.InvalidBusStationException;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.ResponseKakaoBusApi;
import com.dku.council.infra.bus.model.mapper.BusResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoBusService implements BusArrivalInfoService {

    private final WebClient webClient;

    @Value("${bus.kakao.api-path}")
    private final String apiPath;


    public List<BusArrival> retrieveBusArrival(String stationId) {
        try {
            ResponseKakaoBusApi response = request(stationId);

            if (response == null) {
                throw new UnexpectedResponseException("Failed response");
            }

            String name = response.getName();
            if (name == null) {
                throw new InvalidBusStationException();
            }

            return response.getLines().stream()
                    .filter(ResponseKakaoBusApi.BusLine::isRunning)
                    .map(BusResponseMapper::to)
                    .collect(Collectors.toList());
        } catch (Throwable e) {
            throw new CannotGetBusArrivalException(e);
        }
    }

    @Override
    public String getBusId(String busNo) {
        return "K" + busNo;
    }

    private ResponseKakaoBusApi request(String stationId) {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiPath)
                .queryParam("busstopid", stationId)
                .build()
                .toUri();

        return webClient.mutate()
                .build().get()
                .uri(uri)
                .header("Referer", "https://map.kakao.com/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36")
                .retrieve()
                .bodyToMono(ResponseKakaoBusApi.class)
                .block();
    }
}
