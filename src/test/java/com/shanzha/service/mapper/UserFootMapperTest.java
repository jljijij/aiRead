package com.shanzha.service.mapper;

import static com.shanzha.domain.UserFootAsserts.*;
import static com.shanzha.domain.UserFootTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserFootMapperTest {

    private UserFootMapper userFootMapper;

    @BeforeEach
    void setUp() {
        userFootMapper = new UserFootMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserFootSample1();
        var actual = userFootMapper.toEntity(userFootMapper.toDto(expected));
        assertUserFootAllPropertiesEquals(expected, actual);
    }
}
