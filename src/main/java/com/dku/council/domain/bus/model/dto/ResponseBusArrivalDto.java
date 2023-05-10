package com.dku.council.domain.bus.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

import static com.dku.council.global.config.jackson.JacksonDateTimeFormatter.DATE_TIME_FORMAT_PATTERN;

@Getter
@RequiredArgsConstructor
public class ResponseBusArrivalDto {

    @Schema(description = "도착 시간이 계산된 시각", example = "2022-03-01 11:31:11")
    @JsonFormat(pattern = DATE_TIME_FORMAT_PATTERN, timezone = "Asia/Seoul")
    private final Instant capturedAt;
    private final List<BusArrivalDto> busArrivalList;
}
