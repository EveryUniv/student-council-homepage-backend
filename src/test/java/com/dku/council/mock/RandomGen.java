package com.dku.council.mock;

import java.util.Random;
import java.util.UUID;

public class RandomGen {
    private static final Random RANDOM = new Random();

    public static long nextLong() {
        return RANDOM.nextLong();
    }

    public static int nextInt() {
        return RANDOM.nextInt();
    }

    public static String nextUUID() {
        return UUID.randomUUID().toString();
    }
}
