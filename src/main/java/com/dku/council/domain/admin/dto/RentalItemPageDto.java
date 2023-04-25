package com.dku.council.domain.admin.dto;

import com.dku.council.domain.rental.model.entity.RentalItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RentalItemPageDto {
    private final Long id;
    private final String name;
    private final int remaining;
    private final boolean isActive;

    public RentalItemPageDto(RentalItem item){
        this.id = item.getId();
        this.name = item.getName();
        this.remaining = item.getRemaining();
        this.isActive = item.isActive();
    }
}
