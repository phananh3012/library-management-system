package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookCopyTestSamples.*;
import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.CheckoutTestSamples.*;
import static com.mycompany.myapp.domain.HoldTestSamples.*;
import static com.mycompany.myapp.domain.PublisherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookCopyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookCopy.class);
        BookCopy bookCopy1 = getBookCopySample1();
        BookCopy bookCopy2 = new BookCopy();
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);

        bookCopy2.setId(bookCopy1.getId());
        assertThat(bookCopy1).isEqualTo(bookCopy2);

        bookCopy2 = getBookCopySample2();
        assertThat(bookCopy1).isNotEqualTo(bookCopy2);
    }

    @Test
    void checkoutTest() throws Exception {
        BookCopy bookCopy = getBookCopyRandomSampleGenerator();
        Checkout checkoutBack = getCheckoutRandomSampleGenerator();

        bookCopy.addCheckout(checkoutBack);
        assertThat(bookCopy.getCheckouts()).containsOnly(checkoutBack);
        assertThat(checkoutBack.getBookCopy()).isEqualTo(bookCopy);

        bookCopy.removeCheckout(checkoutBack);
        assertThat(bookCopy.getCheckouts()).doesNotContain(checkoutBack);
        assertThat(checkoutBack.getBookCopy()).isNull();

        bookCopy.checkouts(new HashSet<>(Set.of(checkoutBack)));
        assertThat(bookCopy.getCheckouts()).containsOnly(checkoutBack);
        assertThat(checkoutBack.getBookCopy()).isEqualTo(bookCopy);

        bookCopy.setCheckouts(new HashSet<>());
        assertThat(bookCopy.getCheckouts()).doesNotContain(checkoutBack);
        assertThat(checkoutBack.getBookCopy()).isNull();
    }

    @Test
    void holdTest() throws Exception {
        BookCopy bookCopy = getBookCopyRandomSampleGenerator();
        Hold holdBack = getHoldRandomSampleGenerator();

        bookCopy.addHold(holdBack);
        assertThat(bookCopy.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getBookCopy()).isEqualTo(bookCopy);

        bookCopy.removeHold(holdBack);
        assertThat(bookCopy.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getBookCopy()).isNull();

        bookCopy.holds(new HashSet<>(Set.of(holdBack)));
        assertThat(bookCopy.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getBookCopy()).isEqualTo(bookCopy);

        bookCopy.setHolds(new HashSet<>());
        assertThat(bookCopy.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getBookCopy()).isNull();
    }

    @Test
    void publisherTest() throws Exception {
        BookCopy bookCopy = getBookCopyRandomSampleGenerator();
        Publisher publisherBack = getPublisherRandomSampleGenerator();

        bookCopy.setPublisher(publisherBack);
        assertThat(bookCopy.getPublisher()).isEqualTo(publisherBack);

        bookCopy.publisher(null);
        assertThat(bookCopy.getPublisher()).isNull();
    }

    @Test
    void bookTest() throws Exception {
        BookCopy bookCopy = getBookCopyRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        bookCopy.setBook(bookBack);
        assertThat(bookCopy.getBook()).isEqualTo(bookBack);

        bookCopy.book(null);
        assertThat(bookCopy.getBook()).isNull();
    }
}
