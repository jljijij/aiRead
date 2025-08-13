package com.shanzha.domain;

import static com.shanzha.domain.NovelTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NovelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Novel.class);
        Novel novel1 = getNovelSample1();
        Novel novel2 = new Novel();
        assertThat(novel1).isNotEqualTo(novel2);

        novel2.setId(novel1.getId());
        assertThat(novel1).isEqualTo(novel2);

        novel2 = getNovelSample2();
        assertThat(novel1).isNotEqualTo(novel2);
    }
}
