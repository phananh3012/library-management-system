package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CheckoutAsserts.*;
import static com.mycompany.myapp.domain.CheckoutTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckoutMapperTest {

    private CheckoutMapper checkoutMapper;

    @BeforeEach
    void setUp() {
        checkoutMapper = new CheckoutMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCheckoutSample1();
        var actual = checkoutMapper.toEntity(checkoutMapper.toDto(expected));
        assertCheckoutAllPropertiesEquals(expected, actual);
    }
}
