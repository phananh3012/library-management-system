package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "book")
    @JsonIgnoreProperties(value = { "checkouts", "holds", "publisher", "book" }, allowSetters = true)
    private Set<BookCopy> bookCopies = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "waitlist", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "patron_id"))
    @JsonIgnoreProperties(value = { "notifications", "checkouts", "holds", "books" }, allowSetters = true)
    private Set<PatronAccount> patronAccounts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "books")
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Category category;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<BookCopy> getBookCopies() {
        return this.bookCopies;
    }

    public void setBookCopies(Set<BookCopy> bookCopies) {
        if (this.bookCopies != null) {
            this.bookCopies.forEach(i -> i.setBook(null));
        }
        if (bookCopies != null) {
            bookCopies.forEach(i -> i.setBook(this));
        }
        this.bookCopies = bookCopies;
    }

    public Book bookCopies(Set<BookCopy> bookCopies) {
        this.setBookCopies(bookCopies);
        return this;
    }

    public Book addBookCopy(BookCopy bookCopy) {
        this.bookCopies.add(bookCopy);
        bookCopy.setBook(this);
        return this;
    }

    public Book removeBookCopy(BookCopy bookCopy) {
        this.bookCopies.remove(bookCopy);
        bookCopy.setBook(null);
        return this;
    }

    public Set<PatronAccount> getPatronAccounts() {
        return this.patronAccounts;
    }

    public void setPatronAccounts(Set<PatronAccount> patronAccounts) {
        this.patronAccounts = patronAccounts;
    }

    public Book patronAccounts(Set<PatronAccount> patronAccounts) {
        this.setPatronAccounts(patronAccounts);
        return this;
    }

    public Book addPatronAccount(PatronAccount patronAccount) {
        this.patronAccounts.add(patronAccount);
        return this;
    }

    public Book removePatronAccount(PatronAccount patronAccount) {
        this.patronAccounts.remove(patronAccount);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        if (this.authors != null) {
            this.authors.forEach(i -> i.removeBook(this));
        }
        if (authors != null) {
            authors.forEach(i -> i.addBook(this));
        }
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Book category(Category category) {
        this.setCategory(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return getId() != null && getId().equals(((Book) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
