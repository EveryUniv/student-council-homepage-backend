package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.Bus;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import com.dku.council.infra.bus.service.api.GGBusService;
import com.dku.council.infra.bus.service.api.KakaoBusService;
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

    private final GGBusService ggBusService;
    private final KakaoBusService kakaoBusService;

    /**
     * 버스 도착 정보를 OpenAPI를 통해 가져옵니다.
     * 24번은 Kakao Map에서 가져오고, 그 외의 버스들은 OpenAPI를 통해 가져옵니다.
     *
     * @param station 정류소
     * @return 버스 도착정보 목록 (도착정보가 없는 버스는 List에 포함되지 않음)
     */
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        List<BusArrival> ggbusList = ggBusService.retrieveBusArrival(station.getGgNodeId());
        List<BusArrival> kakaoBusList = kakaoBusService.retrieveBusArrival(station.getKakaoNodeId());

        Set<String> busNameSet = Arrays.stream(Bus.values())
                .map(Bus::name)
                .map(name -> name.replaceAll("_", "-"))
                .collect(Collectors.toSet());

        Stream<BusArrival> ggbusStream = ggbusList.stream()
                .filter(b -> busNameSet.contains(ggBusService.getBusId(b.getBusNo())));

        Stream<BusArrival> kakaoBusStream = kakaoBusList.stream()
                .filter(b -> busNameSet.contains(kakaoBusService.getBusId(b.getBusNo())));

        return Stream.concat(ggbusStream, kakaoBusStream)
                .collect(Collectors.toList());
    }
}
