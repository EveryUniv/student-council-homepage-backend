package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.Bus;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.model.BusStatus;
import com.dku.council.infra.bus.predict.BusArrivalPredictService;
import com.dku.council.infra.bus.provider.BusArrivalProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OpenApiBusService {

    private final Clock clock;
    private final BusArrivalPredictService predictService;
    private final List<BusArrivalProvider> providers;

    /**
     * 버스 도착 정보를 OpenAPI를 통해 가져옵니다.
     *
     * @param station 정류소
     * @return 버스 도착정보 목록
     */
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        Stream<BusArrival> stream = null;
        for (BusArrivalProvider provider : providers) {
            List<BusArrival> busArrivalList = provider.retrieveBusArrival(station);
            busArrivalList = new ArrayList<>(busArrivalList);

            appendOtherArrivals(provider, busArrivalList, station);

            Stream<BusArrival> resultStream = busArrivalList.stream()
                    .filter(arrival -> filterBus(provider.getProviderPrefix(), arrival));

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

    /**
     * 도착 정보가 없는 버스는 직접 예측해서 정보를 제공합니다. (예측도 안되면 STOP으로 추가)
     */
    private void appendOtherArrivals(BusArrivalProvider provider, List<BusArrival> arrivals, BusStation station) {
        List<Bus> busList = station.getBusSet().stream()
                .filter(b -> b.name().startsWith(provider.getProviderPrefix()))
                .filter(b -> arrivals.stream().noneMatch(a ->
                        a.getBusNo().equals(b.getName()) && b.filterStationOrder(a.getStationOrder())
                ))
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now(clock);
        for (Bus bus : busList) {
            Duration remaining = predictService.remainingNextBusArrival(bus.getName(), station, now);
            BusArrival arrival;
            if (remaining != null) {
                arrival = new BusArrival(BusStatus.PREDICT, 0,
                        bus.getPredictionStationOrder(), (int) remaining.getSeconds(), "",
                        0, 0, "", bus.getName());
            } else {
                arrival = BusArrival.stopped(bus.getName());
            }
            arrivals.add(arrival);
        }
    }

    /**
     * 버스 정보를 필터링해서 필요한 정보만 출력합니다. <br>
     * 1. BUS목록에 없는 버스 필터링 <br>
     * 2. station이 지정된 경우 station으로 필터링
     */
    private static boolean filterBus(String providerPrefix, BusArrival arrival) {
        String key = providerPrefix + arrival.getBusNo();
        Integer order = arrival.getStationOrder();
        key = key.replaceAll("-", "_").toUpperCase();

        if (arrival.getStatus() == BusStatus.PREDICT) {
            return true;
        }

        for (Bus bus : Bus.values()) {
            if (bus.name().startsWith(key) && bus.filterStationOrder(order)) {
                return true;
            }
        }

        return false;
    }
}
