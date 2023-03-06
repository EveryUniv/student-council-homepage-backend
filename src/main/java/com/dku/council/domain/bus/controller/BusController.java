package com.dku.council.domain.bus.controller;

import com.dku.council.domain.bus.exception.InvalidBusStationException;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.domain.bus.model.dto.RequestBusArrivalDto;
import com.dku.council.domain.bus.model.dto.ResponseBusArrivalDto;
import com.dku.council.domain.bus.service.BusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "버스 도착 정보", description = "버스 도착 정보 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bus")
public class BusController {

    private final BusService service;

    /**
     * 예상 버스 도착 시간을 조회합니다.
     *
     * @return 버스 도착 예상 시간 목록
     */
    @GetMapping
    public ResponseBusArrivalDto listBusArrivalTime(@Valid @RequestBody RequestBusArrivalDto dto) {
        BusStation station = BusStation.of(dto.getStation());
        if (station == null) {
            throw new InvalidBusStationException();
        }
        return service.listBusArrival(station);
    }
}
