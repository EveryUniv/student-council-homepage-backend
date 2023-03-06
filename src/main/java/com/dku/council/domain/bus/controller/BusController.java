package com.dku.council.domain.bus.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "버스 도착 정보", description = "버스 도착 정보 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bus")
public class BusController {

    /**
     * 예상 버스 도착 시각을 조회합니다.
     */
    @GetMapping
    public void listBusArrivalTime(){

    }
}
