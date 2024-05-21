package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.HoldAsserts.*;
import static com.mycompany.myapp.domain.HoldTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HoldMapperTest {

    private HoldMapper holdMapper;

    @BeforeEach
    void setUp() {
        holdMapper = new HoldMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHoldSample1();
        var actual = holdMapper.toEntity(holdMapper.toDto(expected));
        assertHoldAllPropertiesEquals(expected, actual);
    }
}
