package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.Bus;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.provider.BusArrivalProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OpenApiBusService {

    private final List<BusArrivalProvider> providers;

    /**
     * 버스 도착 정보를 OpenAPI를 통해 가져옵니다.
     *
     * @param station 정류소
     * @return 버스 도착정보 목록 (도착정보가 없는 버스는 List에 포함되지 않음)
     */
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        Set<String> busNameSet = Arrays.stream(Bus.values())
                .map(Bus::name)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Stream<BusArrival> stream = null;
        for (BusArrivalProvider provider : providers) {
            List<BusArrival> busArrivalList = provider.retrieveBusArrival(station);
            Stream<BusArrival> resultStream = busArrivalList.stream()
                    .filter(b -> {
                        String key = provider.getProviderPrefix() + b.getBusNo();
                        key = key.replaceAll("-", "_").toLowerCase();
                        return busNameSet.contains(key);
                    });
            if (stream == null) {
                stream = resultStream;
            } else {
                stream = Stream.concat(stream, resultStream);
            }
        }

        if (stream == null) {
            return List.of();
        }

        return stream.collect(Collectors.toList());
    }
}
