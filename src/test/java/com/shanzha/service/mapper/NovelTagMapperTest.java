package com.shanzha.service.mapper;

import static com.shanzha.domain.NovelTagAsserts.*;
import static com.shanzha.domain.NovelTagTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NovelTagMapperTest {

    private NovelTagMapper novelTagMapper;

    @BeforeEach
    void setUp() {
        novelTagMapper = new NovelTagMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNovelTagSample1();
        var actual = novelTagMapper.toEntity(novelTagMapper.toDto(expected));
        assertNovelTagAllPropertiesEquals(expected, actual);
    }
}
