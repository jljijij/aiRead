package com.shanzha.service.mapper;

import static com.shanzha.domain.ChapterContentAsserts.*;
import static com.shanzha.domain.ChapterContentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterContentMapperTest {

    private ChapterContentMapper chapterContentMapper;

    @BeforeEach
    void setUp() {
        chapterContentMapper = new ChapterContentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChapterContentSample1();
        var actual = chapterContentMapper.toEntity(chapterContentMapper.toDto(expected));
        assertChapterContentAllPropertiesEquals(expected, actual);
    }
}
