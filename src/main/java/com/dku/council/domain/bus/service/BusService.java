package com.dku.council.domain.bus.service;

import com.dku.council.domain.bus.model.BusArrival;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.model.dto.BusArrivalDto;
import com.dku.council.domain.bus.model.dto.ResponseBusArrivalDto;
import com.dku.council.domain.bus.model.repository.CachedBusArrivals;
import com.dku.council.domain.bus.repository.BusArrivalMemoryRepository;
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
    private final BusArrivalMemoryRepository memoryRepository;

    public ResponseBusArrivalDto listBusArrival(BusStation station) {
        Instant now = Instant.now(clock);
        String stationId = station.getNodeId();

        CachedBusArrivals cached = memoryRepository.getArrivals(stationId, now);

        if (cached == null) {
            List<BusArrival> arrivals = openApiBusService.retrieveBusArrival(stationId);
            cached = memoryRepository.cacheArrivals(stationId, arrivals, now);
        }

        List<BusArrivalDto> busArrivalDtos = cached.getArrivals().stream()
                .map(BusArrivalDto::new)
                .collect(Collectors.toList());
        return new ResponseBusArrivalDto(cached.getCapturedAt(), busArrivalDtos);
    }
}
