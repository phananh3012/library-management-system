package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AuthorTestSamples.*;
import static com.mycompany.myapp.domain.BookCopyTestSamples.*;
import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.CategoryTestSamples.*;
import static com.mycompany.myapp.domain.PatronAccountTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = getBookSample1();
        Book book2 = new Book();
        assertThat(book1).isNotEqualTo(book2);

        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);

        book2 = getBookSample2();
        assertThat(book1).isNotEqualTo(book2);
    }

    @Test
    void bookCopyTest() throws Exception {
        Book book = getBookRandomSampleGenerator();
        BookCopy bookCopyBack = getBookCopyRandomSampleGenerator();

        book.addBookCopy(bookCopyBack);
        assertThat(book.getBookCopies()).containsOnly(bookCopyBack);
        assertThat(bookCopyBack.getBook()).isEqualTo(book);

        book.removeBookCopy(bookCopyBack);
        assertThat(book.getBookCopies()).doesNotContain(bookCopyBack);
        assertThat(bookCopyBack.getBook()).isNull();

        book.bookCopies(new HashSet<>(Set.of(bookCopyBack)));
        assertThat(book.getBookCopies()).containsOnly(bookCopyBack);
        assertThat(bookCopyBack.getBook()).isEqualTo(book);

        book.setBookCopies(new HashSet<>());
        assertThat(book.getBookCopies()).doesNotContain(bookCopyBack);
        assertThat(bookCopyBack.getBook()).isNull();
    }

    @Test
    void patronAccountTest() throws Exception {
        Book book = getBookRandomSampleGenerator();
        PatronAccount patronAccountBack = getPatronAccountRandomSampleGenerator();

        book.addPatronAccount(patronAccountBack);
        assertThat(book.getPatronAccounts()).containsOnly(patronAccountBack);

        book.removePatronAccount(patronAccountBack);
        assertThat(book.getPatronAccounts()).doesNotContain(patronAccountBack);

        book.patronAccounts(new HashSet<>(Set.of(patronAccountBack)));
        assertThat(book.getPatronAccounts()).containsOnly(patronAccountBack);

        book.setPatronAccounts(new HashSet<>());
        assertThat(book.getPatronAccounts()).doesNotContain(patronAccountBack);
    }

    @Test
    void authorTest() throws Exception {
        Book book = getBookRandomSampleGenerator();
        Author authorBack = getAuthorRandomSampleGenerator();

        book.addAuthor(authorBack);
        assertThat(book.getAuthors()).containsOnly(authorBack);
        assertThat(authorBack.getBooks()).containsOnly(book);

        book.removeAuthor(authorBack);
        assertThat(book.getAuthors()).doesNotContain(authorBack);
        assertThat(authorBack.getBooks()).doesNotContain(book);

        book.authors(new HashSet<>(Set.of(authorBack)));
        assertThat(book.getAuthors()).containsOnly(authorBack);
        assertThat(authorBack.getBooks()).containsOnly(book);

        book.setAuthors(new HashSet<>());
        assertThat(book.getAuthors()).doesNotContain(authorBack);
        assertThat(authorBack.getBooks()).doesNotContain(book);
    }

    @Test
    void categoryTest() throws Exception {
        Book book = getBookRandomSampleGenerator();
        Category categoryBack = getCategoryRandomSampleGenerator();

        book.setCategory(categoryBack);
        assertThat(book.getCategory()).isEqualTo(categoryBack);

        book.category(null);
        assertThat(book.getCategory()).isNull();
    }
}
