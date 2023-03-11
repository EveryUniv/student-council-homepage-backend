package com.dku.council.mock;

import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.FieldReflector;

import java.util.ArrayList;
import java.util.List;

public class RentalItemMock {
    public static final String NAME = "testitem";
    public static final int AVAILABLE = 17;


    public static List<RentalItem> createDisabledList(int size) {
        return createList(size, false);
    }

    public static List<RentalItem> createList(int size) {
        return createList(size, true);
    }

    private static List<RentalItem> createList(int size, boolean enabled) {
        List<RentalItem> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            RentalItem item = new RentalItem("item" + i, i + 10);
            if (!enabled) {
                FieldReflector.inject(RentalItem.class, item, "isActive", false);
            }
            result.add(item);
        }
        return result;
    }

    public static RentalItem create(Long id, String name, int available) {
        RentalItem item = new RentalItem(name, available);
        EntityUtil.injectId(RentalItem.class, item, id);
        return item;
    }

    public static RentalItem create(Long id) {
        return create(id, NAME, AVAILABLE);
    }
}
