package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookCopyTestSamples.*;
import static com.mycompany.myapp.domain.CheckoutTestSamples.*;
import static com.mycompany.myapp.domain.PatronAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CheckoutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Checkout.class);
        Checkout checkout1 = getCheckoutSample1();
        Checkout checkout2 = new Checkout();
        assertThat(checkout1).isNotEqualTo(checkout2);

        checkout2.setId(checkout1.getId());
        assertThat(checkout1).isEqualTo(checkout2);

        checkout2 = getCheckoutSample2();
        assertThat(checkout1).isNotEqualTo(checkout2);
    }

    @Test
    void bookCopyTest() throws Exception {
        Checkout checkout = getCheckoutRandomSampleGenerator();
        BookCopy bookCopyBack = getBookCopyRandomSampleGenerator();

        checkout.setBookCopy(bookCopyBack);
        assertThat(checkout.getBookCopy()).isEqualTo(bookCopyBack);

        checkout.bookCopy(null);
        assertThat(checkout.getBookCopy()).isNull();
    }

    @Test
    void patronAccountTest() throws Exception {
        Checkout checkout = getCheckoutRandomSampleGenerator();
        PatronAccount patronAccountBack = getPatronAccountRandomSampleGenerator();

        checkout.setPatronAccount(patronAccountBack);
        assertThat(checkout.getPatronAccount()).isEqualTo(patronAccountBack);

        checkout.patronAccount(null);
        assertThat(checkout.getPatronAccount()).isNull();
    }
}
