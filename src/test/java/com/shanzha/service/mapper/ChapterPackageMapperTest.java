package com.shanzha.service.mapper;

import static com.shanzha.domain.ChapterPackageAsserts.*;
import static com.shanzha.domain.ChapterPackageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterPackageMapperTest {

    private ChapterPackageMapper chapterPackageMapper;

    @BeforeEach
    void setUp() {
        chapterPackageMapper = new ChapterPackageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChapterPackageSample1();
        var actual = chapterPackageMapper.toEntity(chapterPackageMapper.toDto(expected));
        assertChapterPackageAllPropertiesEquals(expected, actual);
    }
}
