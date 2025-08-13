package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPackageItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPackageItemDTO.class);
        ChapterPackageItemDTO chapterPackageItemDTO1 = new ChapterPackageItemDTO();
        chapterPackageItemDTO1.setId(1L);
        ChapterPackageItemDTO chapterPackageItemDTO2 = new ChapterPackageItemDTO();
        assertThat(chapterPackageItemDTO1).isNotEqualTo(chapterPackageItemDTO2);
        chapterPackageItemDTO2.setId(chapterPackageItemDTO1.getId());
        assertThat(chapterPackageItemDTO1).isEqualTo(chapterPackageItemDTO2);
        chapterPackageItemDTO2.setId(2L);
        assertThat(chapterPackageItemDTO1).isNotEqualTo(chapterPackageItemDTO2);
        chapterPackageItemDTO1.setId(null);
        assertThat(chapterPackageItemDTO1).isNotEqualTo(chapterPackageItemDTO2);
    }
}
