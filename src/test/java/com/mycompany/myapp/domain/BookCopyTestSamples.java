package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookCopyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookCopy getBookCopySample1() {
        return new BookCopy().id(1L).yearPublished(1);
    }

    public static BookCopy getBookCopySample2() {
        return new BookCopy().id(2L).yearPublished(2);
    }

    public static BookCopy getBookCopyRandomSampleGenerator() {
        return new BookCopy().id(longCount.incrementAndGet()).yearPublished(intCount.incrementAndGet());
    }
}
