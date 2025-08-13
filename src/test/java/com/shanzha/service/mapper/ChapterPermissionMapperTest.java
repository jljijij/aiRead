package com.shanzha.service.mapper;

import static com.shanzha.domain.ChapterPermissionAsserts.*;
import static com.shanzha.domain.ChapterPermissionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChapterPermissionMapperTest {

    private ChapterPermissionMapper chapterPermissionMapper;

    @BeforeEach
    void setUp() {
        chapterPermissionMapper = new ChapterPermissionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChapterPermissionSample1();
        var actual = chapterPermissionMapper.toEntity(chapterPermissionMapper.toDto(expected));
        assertChapterPermissionAllPropertiesEquals(expected, actual);
    }
}
