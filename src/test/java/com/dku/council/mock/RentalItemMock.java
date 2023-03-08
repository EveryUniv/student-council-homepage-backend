package com.dku.council.mock;

import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.util.FieldInjector;

import java.util.ArrayList;
import java.util.List;

public class RentalItemMock {
    public static List<RentalItem> createList(int size) {
        List<RentalItem> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new RentalItem("item" + i, i));
        }
        return result;
    }

    public static RentalItem create(Long id, String name, int available) {
        RentalItem item = new RentalItem(name, available);
        FieldInjector.injectId(RentalItem.class, item, id);
        return item;
    }
}
