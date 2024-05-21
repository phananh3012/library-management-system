package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookTestSamples.*;
import static com.mycompany.myapp.domain.CategoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = getCategorySample1();
        Category category2 = new Category();
        assertThat(category1).isNotEqualTo(category2);

        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);

        category2 = getCategorySample2();
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    void bookTest() throws Exception {
        Category category = getCategoryRandomSampleGenerator();
        Book bookBack = getBookRandomSampleGenerator();

        category.addBook(bookBack);
        assertThat(category.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getCategory()).isEqualTo(category);

        category.removeBook(bookBack);
        assertThat(category.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getCategory()).isNull();

        category.books(new HashSet<>(Set.of(bookBack)));
        assertThat(category.getBooks()).containsOnly(bookBack);
        assertThat(bookBack.getCategory()).isEqualTo(category);

        category.setBooks(new HashSet<>());
        assertThat(category.getBooks()).doesNotContain(bookBack);
        assertThat(bookBack.getCategory()).isNull();
    }
}
