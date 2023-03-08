package com.dku.council.mock;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalMock {
    public static List<Rental> createList(RentalItem item, int size) {
        List<Rental> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Rental rental = Rental.builder()
                    .item(item)
                    .user(UserMock.create())
                    .title("title")
                    .body("body")
                    .rentalStart(LocalDateTime.MIN)
                    .rentalEnd(LocalDateTime.MAX)
                    .userClass(RentalUserClass.INDIVIDUAL)
                    .build();
            result.add(rental);
        }
        return result;
    }
}
