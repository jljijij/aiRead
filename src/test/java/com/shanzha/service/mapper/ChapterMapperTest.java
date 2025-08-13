package com.shanzha.service.mapper;

import static com.shanzha.domain.ChapterAsserts.*;
import static com.shanzha.domain.ChapterTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterMapperTest {

    private ChapterMapper chapterMapper;

    @BeforeEach
    void setUp() {
        chapterMapper = new ChapterMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChapterSample1();
        var actual = chapterMapper.toEntity(chapterMapper.toDto(expected));
        assertChapterAllPropertiesEquals(expected, actual);
    }
}
