package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Hold} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HoldDTO implements Serializable {

    private Long id;

    private Instant startTime;

    private Instant endTime;

    private BookCopyDTO bookCopy;

    private PatronAccountDTO patronAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BookCopyDTO getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopyDTO bookCopy) {
        this.bookCopy = bookCopy;
    }

    public PatronAccountDTO getPatronAccount() {
        return patronAccount;
    }

    public void setPatronAccount(PatronAccountDTO patronAccount) {
        this.patronAccount = patronAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HoldDTO)) {
            return false;
        }

        HoldDTO holdDTO = (HoldDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, holdDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HoldDTO{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", bookCopy=" + getBookCopy() +
            ", patronAccount=" + getPatronAccount() +
            "}";
    }
}
