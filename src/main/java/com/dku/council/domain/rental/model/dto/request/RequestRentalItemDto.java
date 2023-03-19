package com.dku.council.domain.rental.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestRentalItemDto {

    @Schema(description = "물품 이름", example = "물품")
    private final String itemName;

    @Schema(description = "대여 가능 개수")
    private final Integer available;
}
