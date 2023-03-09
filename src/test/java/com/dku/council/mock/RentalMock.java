package com.dku.council.mock;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalMock {
    public static final String TITLE = "title";
    public static final String BODY = "body";

    public static List<Rental> createDisabledList(RentalItem item, int size) {
        List<Rental> list = createList(item, size);
        for (Rental rental : list) {
            FieldInjector.inject(Rental.class, rental, "isActive", false);
        }
        return list;
    }

    public static List<Rental> createList(RentalItem item, int size) {
        List<Rental> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Rental rental = Rental.builder()
                    .item(item)
                    .user(UserMock.create())
                    .title(TITLE)
                    .body(BODY)
                    .rentalStart(LocalDateTime.MIN)
                    .rentalEnd(LocalDateTime.MAX)
                    .userClass(RentalUserClass.INDIVIDUAL)
                    .build();
            result.add(rental);
        }
        return result;
    }

    public static Rental create(Long id, User user, RentalItem item) {
        Rental rental = Rental.builder()
                .item(item)
                .user(user)
                .title(TITLE)
                .body(BODY)
                .rentalStart(LocalDateTime.MIN)
                .rentalEnd(LocalDateTime.MAX)
                .userClass(RentalUserClass.INDIVIDUAL)
                .build();
        FieldInjector.injectId(Rental.class, rental, id);
        return rental;
    }
}
