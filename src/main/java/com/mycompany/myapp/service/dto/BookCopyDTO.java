package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BookCopy} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopyDTO implements Serializable {

    private Long id;

    private Integer yearPublished;

    private PublisherDTO publisher;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public PublisherDTO getPublisher() {
        return publisher;
    }

    public void setPublisher(PublisherDTO publisher) {
        this.publisher = publisher;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopyDTO)) {
            return false;
        }

        BookCopyDTO bookCopyDTO = (BookCopyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookCopyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopyDTO{" +
            "id=" + getId() +
            ", yearPublished=" + getYearPublished() +
            ", publisher=" + getPublisher() +
            ", book=" + getBook() +
            "}";
    }
}
