package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AuthorTestSamples.*;
import static com.mycompany.myapp.domain.BookTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AuthorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Author.class);
        Author author1 = getAuthorSample1();
        Author author2 = new Author();
        assertThat(author1).isNotEqualTo(author2);

        author2.setId(author1.getId());
        assertThat(author1).isEqualTo(author2);

        author2 = getAuthorSample2();
        assertThat(author1).isNotEqualTo(author2);
    }

    @Test
    void bookTest() throws Exception {
        Author author = getAuthorRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        author.addBook(bookBack);
        assertThat(author.getBooks()).containsOnly(bookBack);

        author.removeBook(bookBack);
        assertThat(author.getBooks()).doesNotContain(bookBack);

        author.books(new HashSet<>(Set.of(bookBack)));
        assertThat(author.getBooks()).containsOnly(bookBack);

        author.setBooks(new HashSet<>());
        assertThat(author.getBooks()).doesNotContain(bookBack);
    }
}
