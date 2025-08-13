package com.shanzha.service.mapper;

import static com.shanzha.domain.ChapterPackageItemAsserts.*;
import static com.shanzha.domain.ChapterPackageItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterPackageItemMapperTest {

    private ChapterPackageItemMapper chapterPackageItemMapper;

    @BeforeEach
    void setUp() {
        chapterPackageItemMapper = new ChapterPackageItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChapterPackageItemSample1();
        var actual = chapterPackageItemMapper.toEntity(chapterPackageItemMapper.toDto(expected));
        assertChapterPackageItemAllPropertiesEquals(expected, actual);
    }
}
