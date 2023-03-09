package com.dku.council.domain.bus.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ResponseBusArrivalDto {

    @Schema(description = "도착 시간이 계산된 시각", example = "2022-03-01 11:31:11")
    private final Instant capturedAt;
    private final List<BusArrivalDto> busArrivalList;
}
