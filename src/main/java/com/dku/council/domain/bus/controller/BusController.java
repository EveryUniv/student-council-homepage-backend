package com.dku.council.domain.bus.controller;

import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.model.dto.ResponseBusArrivalDto;
import com.dku.council.domain.bus.service.BusService;
import com.dku.council.infra.bus.exception.InvalidBusStationException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Tag(name = "버스 도착 정보", description = "버스 도착 정보 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bus")
public class BusController {

    private final BusService service;

    /**
     * 예상 버스 도착 시간을 조회합니다.
     * 캐싱이 적용됩니다. 데이터는 최신이 아닐 수 있으며, 데이터 가져온 시점은 capturedAt을 통해 알 수 있습니다.
     *
     * @param stationName 버스 정류장 이름. 가능한 값: 단국대정문, 곰상
     * @return 버스 도착 예상 시간 목록
     */
    @GetMapping
    public ResponseBusArrivalDto listBusArrivalTime(@NotBlank @RequestParam String stationName) {
        BusStation station = BusStation.of(stationName);
        if (station == null) {
            throw new InvalidBusStationException();
        }
        return service.listBusArrival(station);
    }
}
