package com.shanzha.domain;

import static com.shanzha.domain.NovelTagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NovelTagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NovelTag.class);
        NovelTag novelTag1 = getNovelTagSample1();
        NovelTag novelTag2 = new NovelTag();
        assertThat(novelTag1).isNotEqualTo(novelTag2);

        novelTag2.setId(novelTag1.getId());
        assertThat(novelTag1).isEqualTo(novelTag2);

        novelTag2 = getNovelTagSample2();
        assertThat(novelTag1).isNotEqualTo(novelTag2);
    }
}
