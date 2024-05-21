package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PatronAccountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PatronAccount getPatronAccountSample1() {
        return new PatronAccount()
            .id(1L)
            .cardNumber("cardNumber1")
            .firstName("firstName1")
            .surname("surname1")
            .email("email1")
            .status("status1");
    }

    public static PatronAccount getPatronAccountSample2() {
        return new PatronAccount()
            .id(2L)
            .cardNumber("cardNumber2")
            .firstName("firstName2")
            .surname("surname2")
            .email("email2")
            .status("status2");
    }

    public static PatronAccount getPatronAccountRandomSampleGenerator() {
        return new PatronAccount()
            .id(longCount.incrementAndGet())
            .cardNumber(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .surname(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
