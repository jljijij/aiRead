package com.shanzha.service.mapper;

import static com.shanzha.domain.UserAiHistoryAsserts.*;
import static com.shanzha.domain.UserAiHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAiHistoryMapperTest {

    private UserAiHistoryMapper userAiHistoryMapper;

    @BeforeEach
    void setUp() {
        userAiHistoryMapper = new UserAiHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserAiHistorySample1();
        var actual = userAiHistoryMapper.toEntity(userAiHistoryMapper.toDto(expected));
        assertUserAiHistoryAllPropertiesEquals(expected, actual);
    }
}
