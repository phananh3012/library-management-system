package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A PatronAccount.
 */
@Entity
@Table(name = "patron_account")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PatronAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patronAccount")
    @JsonIgnoreProperties(value = { "patronAccount" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patronAccount")
    @JsonIgnoreProperties(value = { "bookCopy", "patronAccount" }, allowSetters = true)
    private Set<Checkout> checkouts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patronAccount")
    @JsonIgnoreProperties(value = { "bookCopy", "patronAccount" }, allowSetters = true)
    private Set<Hold> holds = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "patronAccounts")
    @JsonIgnoreProperties(value = { "bookCopies", "patronAccounts", "authors", "category" }, allowSetters = true)
    private Set<Book> books = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PatronAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public PatronAccount cardNumber(String cardNumber) {
        this.setCardNumber(cardNumber);
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public PatronAccount firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public PatronAccount surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return this.email;
    }

    public PatronAccount email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return this.status;
    }

    public PatronAccount status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setPatronAccount(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setPatronAccount(this));
        }
        this.notifications = notifications;
    }

    public PatronAccount notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public PatronAccount addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setPatronAccount(this);
        return this;
    }

    public PatronAccount removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setPatronAccount(null);
        return this;
    }

    public Set<Checkout> getCheckouts() {
        return this.checkouts;
    }

    public void setCheckouts(Set<Checkout> checkouts) {
        if (this.checkouts != null) {
            this.checkouts.forEach(i -> i.setPatronAccount(null));
        }
        if (checkouts != null) {
            checkouts.forEach(i -> i.setPatronAccount(this));
        }
        this.checkouts = checkouts;
    }

    public PatronAccount checkouts(Set<Checkout> checkouts) {
        this.setCheckouts(checkouts);
        return this;
    }

    public PatronAccount addCheckout(Checkout checkout) {
        this.checkouts.add(checkout);
        checkout.setPatronAccount(this);
        return this;
    }

    public PatronAccount removeCheckout(Checkout checkout) {
        this.checkouts.remove(checkout);
        checkout.setPatronAccount(null);
        return this;
    }

    public Set<Hold> getHolds() {
        return this.holds;
    }

    public void setHolds(Set<Hold> holds) {
        if (this.holds != null) {
            this.holds.forEach(i -> i.setPatronAccount(null));
        }
        if (holds != null) {
            holds.forEach(i -> i.setPatronAccount(this));
        }
        this.holds = holds;
    }

    public PatronAccount holds(Set<Hold> holds) {
        this.setHolds(holds);
        return this;
    }

    public PatronAccount addHold(Hold hold) {
        this.holds.add(hold);
        hold.setPatronAccount(this);
        return this;
    }

    public PatronAccount removeHold(Hold hold) {
        this.holds.remove(hold);
        hold.setPatronAccount(null);
        return this;
    }

    public Set<Book> getBooks() {
        return this.books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books != null) {
            this.books.forEach(i -> i.removePatronAccount(this));
        }
        if (books != null) {
            books.forEach(i -> i.addPatronAccount(this));
        }
        this.books = books;
    }

    public PatronAccount books(Set<Book> books) {
        this.setBooks(books);
        return this;
    }

    public PatronAccount addBook(Book book) {
        this.books.add(book);
        book.getPatronAccounts().add(this);
        return this;
    }

    public PatronAccount removeBook(Book book) {
        this.books.remove(book);
        book.getPatronAccounts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PatronAccount)) {
            return false;
        }
        return getId() != null && getId().equals(((PatronAccount) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PatronAccount{" +
            "id=" + getId() +
            ", cardNumber='" + getCardNumber() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", surname='" + getSurname() + "'" +
            ", email='" + getEmail() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
