package com.dku.council.mock;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.FieldReflector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalMock {
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final LocalDateTime RENTAL_START = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    public static final LocalDateTime RENTAL_END = LocalDateTime.of(2023, 1, 5, 12, 0, 0);

    public static List<Rental> createDisabledListDummy(RentalItem item, int size) {
        return createDisabledList(item, UserMock.createDummyMajor(), size);
    }

    public static List<Rental> createDisabledList(RentalItem item, User user, int size) {
        List<Rental> list = createList(item, user, size);
        for (Rental rental : list) {
            FieldReflector.inject(Rental.class, rental, "isActive", false);
        }
        return list;
    }

    public static List<Rental> createList(RentalItem item, int size) {
        return createList(item, UserMock.createDummyMajor(), size);
    }

    public static List<Rental> createList(RentalItem item, User user, int size) {
        List<Rental> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(create(user, item));
        }
        return result;
    }

    public static Rental create(User user, RentalItem item) {
        return create(null, user, item);
    }

    public static Rental create(Long id, User user, RentalItem item) {
        Rental rental = Rental.builder()
                .item(item)
                .user(user)
                .title(TITLE)
                .body(BODY)
                .rentalStart(RENTAL_START)
                .rentalEnd(RENTAL_END)
                .userClass(RentalUserClass.INDIVIDUAL)
                .build();
        rental.changeItem(item);
        if (id != null) {
            EntityUtil.injectId(Rental.class, rental, id);
        }
        return rental;
    }
}
