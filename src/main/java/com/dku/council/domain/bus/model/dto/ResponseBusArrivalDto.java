package com.dku.council.domain.bus.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResponseBusArrivalDto {

    private final Instant capturedAt;
    private final List<BusArrivalDto> busArrivalList;
}
