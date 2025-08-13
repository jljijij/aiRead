package com.shanzha.domain;

import static com.shanzha.domain.UserFootTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserFootTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserFoot.class);
        UserFoot userFoot1 = getUserFootSample1();
        UserFoot userFoot2 = new UserFoot();
        assertThat(userFoot1).isNotEqualTo(userFoot2);

        userFoot2.setId(userFoot1.getId());
        assertThat(userFoot1).isEqualTo(userFoot2);

        userFoot2 = getUserFootSample2();
        assertThat(userFoot1).isNotEqualTo(userFoot2);
    }
}
