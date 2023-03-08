package com.dku.council.domain.rental.model.dto;

import com.dku.council.domain.rental.model.entity.RentalItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RentalItemDto {

    @Schema(description = "아이디", example = "1")
    private final Long id;

    @Schema(description = "물품명", example = "우산")
    private final String name;

    @Schema(description = "남은 물품 수", example = "15")
    private final int remaining;


    public RentalItemDto(RentalItem item) {
        this.id = item.getId();
        this.name = item.getName();
        this.remaining = item.getRemaining();
    }
}
