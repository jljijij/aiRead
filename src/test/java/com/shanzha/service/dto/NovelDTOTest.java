package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NovelDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NovelDTO.class);
        NovelDTO novelDTO1 = new NovelDTO();
        novelDTO1.setId(1L);
        NovelDTO novelDTO2 = new NovelDTO();
        assertThat(novelDTO1).isNotEqualTo(novelDTO2);
        novelDTO2.setId(novelDTO1.getId());
        assertThat(novelDTO1).isEqualTo(novelDTO2);
        novelDTO2.setId(2L);
        assertThat(novelDTO1).isNotEqualTo(novelDTO2);
        novelDTO1.setId(null);
        assertThat(novelDTO1).isNotEqualTo(novelDTO2);
    }
}
