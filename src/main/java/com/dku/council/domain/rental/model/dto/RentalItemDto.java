package com.dku.council.domain.rental.model.dto;

import com.dku.council.domain.rental.model.entity.RentalItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RentalItemDto {

    @Schema(description = "아이디", example = "1")
    private final Long id;

    @Schema(description = "물품명", example = "우산")
    private final String name;

    @Schema(description = "남은 물품 수", example = "15")
    private final int remaining;

    @Schema(description = "가장 빨리 반납되는 시각. 물품 수가 없을 경우에만 null이 아닌 값이 들어갑니다.")
    private final LocalDateTime nextAvaliable;


    public RentalItemDto(RentalItem item, LocalDateTime nextAvaliable) {
        this.id = item.getId();
        this.name = item.getName();
        this.remaining = item.getRemaining();
        this.nextAvaliable = nextAvaliable;
    }
}
