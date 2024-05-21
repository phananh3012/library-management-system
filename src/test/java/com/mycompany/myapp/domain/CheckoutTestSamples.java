package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CheckoutTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Checkout getCheckoutSample1() {
        return new Checkout().id(1L);
    }

    public static Checkout getCheckoutSample2() {
        return new Checkout().id(2L);
    }

    public static Checkout getCheckoutRandomSampleGenerator() {
        return new Checkout().id(longCount.incrementAndGet());
    }
}
