package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class BookCopyAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookCopyAllPropertiesEquals(BookCopy expected, BookCopy actual) {
        assertBookCopyAutoGeneratedPropertiesEquals(expected, actual);
        assertBookCopyAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookCopyAllUpdatablePropertiesEquals(BookCopy expected, BookCopy actual) {
        assertBookCopyUpdatableFieldsEquals(expected, actual);
        assertBookCopyUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookCopyAutoGeneratedPropertiesEquals(BookCopy expected, BookCopy actual) {
        assertThat(expected)
            .as("Verify BookCopy auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookCopyUpdatableFieldsEquals(BookCopy expected, BookCopy actual) {
        assertThat(expected)
            .as("Verify BookCopy relevant properties")
            .satisfies(e -> assertThat(e.getYearPublished()).as("check yearPublished").isEqualTo(actual.getYearPublished()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookCopyUpdatableRelationshipsEquals(BookCopy expected, BookCopy actual) {
        assertThat(expected)
            .as("Verify BookCopy relationships")
            .satisfies(e -> assertThat(e.getPublisher()).as("check publisher").isEqualTo(actual.getPublisher()))
            .satisfies(e -> assertThat(e.getBook()).as("check book").isEqualTo(actual.getBook()));
    }
}
