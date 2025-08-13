package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NovelTagDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NovelTagDTO.class);
        NovelTagDTO novelTagDTO1 = new NovelTagDTO();
        novelTagDTO1.setId(1L);
        NovelTagDTO novelTagDTO2 = new NovelTagDTO();
        assertThat(novelTagDTO1).isNotEqualTo(novelTagDTO2);
        novelTagDTO2.setId(novelTagDTO1.getId());
        assertThat(novelTagDTO1).isEqualTo(novelTagDTO2);
        novelTagDTO2.setId(2L);
        assertThat(novelTagDTO1).isNotEqualTo(novelTagDTO2);
        novelTagDTO1.setId(null);
        assertThat(novelTagDTO1).isNotEqualTo(novelTagDTO2);
    }
}
