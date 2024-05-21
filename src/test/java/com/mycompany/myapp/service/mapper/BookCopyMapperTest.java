package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.BookCopyAsserts.*;
import static com.mycompany.myapp.domain.BookCopyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCopyMapperTest {

    private BookCopyMapper bookCopyMapper;

    @BeforeEach
    void setUp() {
        bookCopyMapper = new BookCopyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBookCopySample1();
        var actual = bookCopyMapper.toEntity(bookCopyMapper.toDto(expected));
        assertBookCopyAllPropertiesEquals(expected, actual);
    }
}
