package com.dku.council.mock;

import com.dku.council.domain.bus.model.BusArrival;

import java.util.ArrayList;
import java.util.List;

public class BusArrivalMock {
    public static BusArrival create() {
        return new BusArrival("PASS",
                5, 10, "경기16바5555",
                7, 15, "경기16바1111",
                "24");
    }

    public static List<BusArrival> createList(int size) {
        List<BusArrival> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(create());
        }
        return result;
    }
}
