package com.shanzha.domain;

import static com.shanzha.domain.ChapterPermissionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPermissionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPermission.class);
        ChapterPermission chapterPermission1 = getChapterPermissionSample1();
        ChapterPermission chapterPermission2 = new ChapterPermission();
        assertThat(chapterPermission1).isNotEqualTo(chapterPermission2);

        chapterPermission2.setId(chapterPermission1.getId());
        assertThat(chapterPermission1).isEqualTo(chapterPermission2);

        chapterPermission2 = getChapterPermissionSample2();
        assertThat(chapterPermission1).isNotEqualTo(chapterPermission2);
    }
}
