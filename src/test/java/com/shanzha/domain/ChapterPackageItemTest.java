package com.shanzha.domain;

import static com.shanzha.domain.ChapterPackageItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPackageItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPackageItem.class);
        ChapterPackageItem chapterPackageItem1 = getChapterPackageItemSample1();
        ChapterPackageItem chapterPackageItem2 = new ChapterPackageItem();
        assertThat(chapterPackageItem1).isNotEqualTo(chapterPackageItem2);

        chapterPackageItem2.setId(chapterPackageItem1.getId());
        assertThat(chapterPackageItem1).isEqualTo(chapterPackageItem2);

        chapterPackageItem2 = getChapterPackageItemSample2();
        assertThat(chapterPackageItem1).isNotEqualTo(chapterPackageItem2);
    }
}
