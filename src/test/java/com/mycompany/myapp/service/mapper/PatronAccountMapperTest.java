package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PatronAccountAsserts.*;
import static com.mycompany.myapp.domain.PatronAccountTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatronAccountMapperTest {

    private PatronAccountMapper patronAccountMapper;

    @BeforeEach
    void setUp() {
        patronAccountMapper = new PatronAccountMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPatronAccountSample1();
        var actual = patronAccountMapper.toEntity(patronAccountMapper.toDto(expected));
        assertPatronAccountAllPropertiesEquals(expected, actual);
    }
}
