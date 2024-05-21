package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.CheckoutTestSamples.*;
import static com.mycompany.myapp.domain.HoldTestSamples.*;
import static com.mycompany.myapp.domain.NotificationTestSamples.*;
import static com.mycompany.myapp.domain.PatronAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PatronAccountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PatronAccount.class);
        PatronAccount patronAccount1 = getPatronAccountSample1();
        PatronAccount patronAccount2 = new PatronAccount();
        assertThat(patronAccount1).isNotEqualTo(patronAccount2);

        patronAccount2.setId(patronAccount1.getId());
        assertThat(patronAccount1).isEqualTo(patronAccount2);

        patronAccount2 = getPatronAccountSample2();
        assertThat(patronAccount1).isNotEqualTo(patronAccount2);
    }

    @Test
    void notificationTest() throws Exception {
        PatronAccount patronAccount = getPatronAccountRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        patronAccount.addNotification(notificationBack);
        assertThat(patronAccount.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.removeNotification(notificationBack);
        assertThat(patronAccount.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getPatronAccount()).isNull();

        patronAccount.notifications(new HashSet<>(Set.of(notificationBack)));
        assertThat(patronAccount.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.setNotifications(new HashSet<>());
        assertThat(patronAccount.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getPatronAccount()).isNull();
    }

    @Test
    void checkoutTest() throws Exception {
        PatronAccount patronAccount = getPatronAccountRandomSampleGenerator();
        Checkout checkoutBack = getCheckoutRandomSampleGenerator();

        patronAccount.addCheckout(checkoutBack);
        assertThat(patronAccount.getCheckouts()).containsOnly(checkoutBack);
        assertThat(checkoutBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.removeCheckout(checkoutBack);
        assertThat(patronAccount.getCheckouts()).doesNotContain(checkoutBack);
        assertThat(checkoutBack.getPatronAccount()).isNull();

        patronAccount.checkouts(new HashSet<>(Set.of(checkoutBack)));
        assertThat(patronAccount.getCheckouts()).containsOnly(checkoutBack);
        assertThat(checkoutBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.setCheckouts(new HashSet<>());
        assertThat(patronAccount.getCheckouts()).doesNotContain(checkoutBack);
        assertThat(checkoutBack.getPatronAccount()).isNull();
    }

    @Test
    void holdTest() throws Exception {
        PatronAccount patronAccount = getPatronAccountRandomSampleGenerator();
        Hold holdBack = getHoldRandomSampleGenerator();

        patronAccount.addHold(holdBack);
        assertThat(patronAccount.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.removeHold(holdBack);
        assertThat(patronAccount.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getPatronAccount()).isNull();

        patronAccount.holds(new HashSet<>(Set.of(holdBack)));
        assertThat(patronAccount.getHolds()).containsOnly(holdBack);
        assertThat(holdBack.getPatronAccount()).isEqualTo(patronAccount);

        patronAccount.setHolds(new HashSet<>());
        assertThat(patronAccount.getHolds()).doesNotContain(holdBack);
        assertThat(holdBack.getPatronAccount()).isNull();
    }

    @Test
    void bookTest() throws Exception {
        PatronAccount patronAccount = getPatronAccountRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        patronAccount.addBook(bookBack);
        assertThat(patronAccount.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getPatronAccounts()).containsOnly(patronAccount);

        patronAccount.removeBook(bookBack);
        assertThat(patronAccount.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getPatronAccounts()).doesNotContain(patronAccount);

        patronAccount.books(new HashSet<>(Set.of(bookBack)));
        assertThat(patronAccount.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getPatronAccounts()).containsOnly(patronAccount);

        patronAccount.setBooks(new HashSet<>());
        assertThat(patronAccount.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getPatronAccounts()).doesNotContain(patronAccount);
    }
}
