package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPackageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPackageDTO.class);
        ChapterPackageDTO chapterPackageDTO1 = new ChapterPackageDTO();
        chapterPackageDTO1.setId(1L);
        ChapterPackageDTO chapterPackageDTO2 = new ChapterPackageDTO();
        assertThat(chapterPackageDTO1).isNotEqualTo(chapterPackageDTO2);
        chapterPackageDTO2.setId(chapterPackageDTO1.getId());
        assertThat(chapterPackageDTO1).isEqualTo(chapterPackageDTO2);
        chapterPackageDTO2.setId(2L);
        assertThat(chapterPackageDTO1).isNotEqualTo(chapterPackageDTO2);
        chapterPackageDTO1.setId(null);
        assertThat(chapterPackageDTO1).isNotEqualTo(chapterPackageDTO2);
    }
}
