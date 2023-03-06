package com.dku.council.infra.bus.service;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.model.BusArrival;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenApiBusService {

    /**
     * 버스 도착 정보를 OpenAPI를 통해 가져옵니다.
     * 24번은 Kakao Map에서 가져오고, 그 외의 버스들은 OpenAPI를 통해 가져옵니다.
     *
     * @param station 정류소
     * @return 버스 도착정보 목록 (도착정보가 없는 버스는 List에 포함되지 않음)
     */
    public List<BusArrival> retrieveBusArrival(BusStation station) {
        return List.of();
    }
}
