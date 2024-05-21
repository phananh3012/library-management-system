package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Checkout.
 */
@Entity
@Table(name = "checkout")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Checkout implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "is_returned")
    private Boolean isReturned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "checkouts", "holds", "publisher", "book" }, allowSetters = true)
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "notifications", "checkouts", "holds", "books" }, allowSetters = true)
    private PatronAccount patronAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Checkout id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Checkout startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public Checkout endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsReturned() {
        return this.isReturned;
    }

    public Checkout isReturned(Boolean isReturned) {
        this.setIsReturned(isReturned);
        return this;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }

    public BookCopy getBookCopy() {
        return this.bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public Checkout bookCopy(BookCopy bookCopy) {
        this.setBookCopy(bookCopy);
        return this;
    }

    public PatronAccount getPatronAccount() {
        return this.patronAccount;
    }

    public void setPatronAccount(PatronAccount patronAccount) {
        this.patronAccount = patronAccount;
    }

    public Checkout patronAccount(PatronAccount patronAccount) {
        this.setPatronAccount(patronAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Checkout)) {
            return false;
        }
        return getId() != null && getId().equals(((Checkout) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Checkout{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isReturned='" + getIsReturned() + "'" +
            "}";
    }
}
