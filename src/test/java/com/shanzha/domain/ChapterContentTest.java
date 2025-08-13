package com.shanzha.domain;

import static com.shanzha.domain.ChapterContentTestSamples.*;
import static com.shanzha.domain.ChapterTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterContentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterContent.class);
        ChapterContent chapterContent1 = getChapterContentSample1();
        ChapterContent chapterContent2 = new ChapterContent();
        assertThat(chapterContent1).isNotEqualTo(chapterContent2);

        chapterContent2.setId(chapterContent1.getId());
        assertThat(chapterContent1).isEqualTo(chapterContent2);

        chapterContent2 = getChapterContentSample2();
        assertThat(chapterContent1).isNotEqualTo(chapterContent2);
    }

    @Test
    void chapterTest() {
        ChapterContent chapterContent = getChapterContentRandomSampleGenerator();
        Chapter chapterBack = getChapterRandomSampleGenerator();

        chapterContent.setChapter(chapterBack);
        assertThat(chapterContent.getChapter()).isEqualTo(chapterBack);

        chapterContent.chapter(null);
        assertThat(chapterContent.getChapter()).isNull();
    }
}
