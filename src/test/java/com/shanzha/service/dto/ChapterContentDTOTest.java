package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterContentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterContentDTO.class);
        ChapterContentDTO chapterContentDTO1 = new ChapterContentDTO();
        chapterContentDTO1.setId(1L);
        ChapterContentDTO chapterContentDTO2 = new ChapterContentDTO();
        assertThat(chapterContentDTO1).isNotEqualTo(chapterContentDTO2);
        chapterContentDTO2.setId(chapterContentDTO1.getId());
        assertThat(chapterContentDTO1).isEqualTo(chapterContentDTO2);
        chapterContentDTO2.setId(2L);
        assertThat(chapterContentDTO1).isNotEqualTo(chapterContentDTO2);
        chapterContentDTO1.setId(null);
        assertThat(chapterContentDTO1).isNotEqualTo(chapterContentDTO2);
    }
}
