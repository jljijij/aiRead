package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChapterPermissionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChapterPermissionDTO.class);
        ChapterPermissionDTO chapterPermissionDTO1 = new ChapterPermissionDTO();
        chapterPermissionDTO1.setId(1L);
        ChapterPermissionDTO chapterPermissionDTO2 = new ChapterPermissionDTO();
        assertThat(chapterPermissionDTO1).isNotEqualTo(chapterPermissionDTO2);
        chapterPermissionDTO2.setId(chapterPermissionDTO1.getId());
        assertThat(chapterPermissionDTO1).isEqualTo(chapterPermissionDTO2);
        chapterPermissionDTO2.setId(2L);
        assertThat(chapterPermissionDTO1).isNotEqualTo(chapterPermissionDTO2);
        chapterPermissionDTO1.setId(null);
        assertThat(chapterPermissionDTO1).isNotEqualTo(chapterPermissionDTO2);
    }
}
