package com.dku.council.domain.admin.dto;

import com.dku.council.domain.rental.model.entity.Rental;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RentalPageDto {
    private final Long id;
    private final LocalDateTime createdAt;
    private final String userClass;
    private final String title;
    private final String itemName;
    private final String userName;
    private final LocalDateTime rentalStart;
    private final LocalDateTime rentalEnd;
    private final boolean isActive;

    public RentalPageDto(Rental rental){
        this.id = rental.getId();
        this.createdAt = rental.getCreatedAt();
        this.userClass = rental.getUserClass().toString();
        this.title = rental.getTitle();
        this.itemName = rental.getItem().getName();
        this.userName = rental.getUser().getName();
        this.rentalStart = rental.getRentalStart();
        this.rentalEnd = rental.getRentalEnd();
        this.isActive = rental.isActive();
    }
}
