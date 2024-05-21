package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class HoldTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Hold getHoldSample1() {
        return new Hold().id(1L);
    }

    public static Hold getHoldSample2() {
        return new Hold().id(2L);
    }

    public static Hold getHoldRandomSampleGenerator() {
        return new Hold().id(longCount.incrementAndGet());
    }
}
