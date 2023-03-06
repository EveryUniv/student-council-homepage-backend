package com.dku.council.domain.bus.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestBusArrivalDto {

    @NotBlank
    @Schema(description = "버스 정류장 이름. 가능한 값: 단국대정문, 곰상", example = "단국대정문")
    private final String station;
}
