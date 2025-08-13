package com.shanzha.domain;

import static com.shanzha.domain.ChapterPackageTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPackageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPackage.class);
        ChapterPackage chapterPackage1 = getChapterPackageSample1();
        ChapterPackage chapterPackage2 = new ChapterPackage();
        assertThat(chapterPackage1).isNotEqualTo(chapterPackage2);

        chapterPackage2.setId(chapterPackage1.getId());
        assertThat(chapterPackage1).isEqualTo(chapterPackage2);

        chapterPackage2 = getChapterPackageSample2();
        assertThat(chapterPackage1).isNotEqualTo(chapterPackage2);
    }
}
