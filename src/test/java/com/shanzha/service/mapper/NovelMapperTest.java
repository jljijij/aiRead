package com.shanzha.service.mapper;

import static com.shanzha.domain.NovelAsserts.*;
import static com.shanzha.domain.NovelTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NovelMapperTest {

    private NovelMapper novelMapper;

    @BeforeEach
    void setUp() {
        novelMapper = new NovelMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNovelSample1();
        var actual = novelMapper.toEntity(novelMapper.toDto(expected));
        assertNovelAllPropertiesEquals(expected, actual);
    }
}
