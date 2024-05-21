package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookCopyTestSamples.*;
import static com.mycompany.myapp.domain.HoldTestSamples.*;
import static com.mycompany.myapp.domain.PatronAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HoldTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hold.class);
        Hold hold1 = getHoldSample1();
        Hold hold2 = new Hold();
        assertThat(hold1).isNotEqualTo(hold2);

        hold2.setId(hold1.getId());
        assertThat(hold1).isEqualTo(hold2);

        hold2 = getHoldSample2();
        assertThat(hold1).isNotEqualTo(hold2);
    }

    @Test
    void bookCopyTest() throws Exception {
        Hold hold = getHoldRandomSampleGenerator();
        BookCopy bookCopyBack = getBookCopyRandomSampleGenerator();

        hold.setBookCopy(bookCopyBack);
        assertThat(hold.getBookCopy()).isEqualTo(bookCopyBack);

        hold.bookCopy(null);
        assertThat(hold.getBookCopy()).isNull();
    }

    @Test
    void patronAccountTest() throws Exception {
        Hold hold = getHoldRandomSampleGenerator();
        PatronAccount patronAccountBack = getPatronAccountRandomSampleGenerator();

        hold.setPatronAccount(patronAccountBack);
        assertThat(hold.getPatronAccount()).isEqualTo(patronAccountBack);

        hold.patronAccount(null);
        assertThat(hold.getPatronAccount()).isNull();
    }
}
