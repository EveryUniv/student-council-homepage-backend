package com.dku.council.mock;

import java.util.Random;

public class RandomGen {
    private static final Random RANDOM = new Random();

    public static Long nextLong() {
        return RANDOM.nextLong();
    }
}
