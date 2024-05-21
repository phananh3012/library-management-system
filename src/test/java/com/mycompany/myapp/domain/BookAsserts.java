package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class BookAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookAllPropertiesEquals(Book expected, Book actual) {
        assertBookAutoGeneratedPropertiesEquals(expected, actual);
        assertBookAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookAllUpdatablePropertiesEquals(Book expected, Book actual) {
        assertBookUpdatableFieldsEquals(expected, actual);
        assertBookUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookAutoGeneratedPropertiesEquals(Book expected, Book actual) {
        assertThat(expected)
            .as("Verify Book auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookUpdatableFieldsEquals(Book expected, Book actual) {
        assertThat(expected)
            .as("Verify Book relevant properties")
            .satisfies(e -> assertThat(e.getTitle()).as("check title").isEqualTo(actual.getTitle()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertBookUpdatableRelationshipsEquals(Book expected, Book actual) {
        assertThat(expected)
            .as("Verify Book relationships")
            .satisfies(e -> assertThat(e.getPatronAccounts()).as("check patronAccounts").isEqualTo(actual.getPatronAccounts()))
            .satisfies(e -> assertThat(e.getAuthors()).as("check authors").isEqualTo(actual.getAuthors()))
            .satisfies(e -> assertThat(e.getCategory()).as("check category").isEqualTo(actual.getCategory()));
    }
}