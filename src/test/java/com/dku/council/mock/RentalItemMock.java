package com.dku.council.mock;

import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class RentalItemMock {
    public static final String NAME = "testitem";
    public static final int AVAILABLE = 17;

    public static List<RentalItem> createList(int size) {
        List<RentalItem> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new RentalItem("item" + i, i + 10));
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
