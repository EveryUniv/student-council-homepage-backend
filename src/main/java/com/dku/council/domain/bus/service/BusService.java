package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.model.CachedBusArrivals;
import com.dku.council.domain.bus.model.dto.BusArrivalDto;
import com.dku.council.domain.bus.model.dto.ResponseBusArrivalDto;
import com.dku.council.domain.bus.repository.BusArrivalRepository;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.OpenApiBusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final Clock clock;
    private final OpenApiBusService openApiBusService;
    private final BusArrivalRepository memoryRepository;

    public ResponseBusArrivalDto listBusArrival(BusStation station) {
        Instant now = Instant.now(clock);
        String stationName = station.name();

        CachedBusArrivals cached = memoryRepository.getArrivals(stationName, now)
                .orElseGet(() -> {
                    List<BusArrival> arrivals = openApiBusService.retrieveBusArrival(station);
                    return memoryRepository.cacheArrivals(stationName, arrivals, now);
                });

        List<BusArrivalDto> busArrivalDtos = cached.getArrivals().stream()
                .map(BusArrivalDto::new)
                .collect(Collectors.toList());
        return new ResponseBusArrivalDto(cached.getCapturedAt(), busArrivalDtos);
    }
}
