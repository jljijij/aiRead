package com.shanzha.domain;

import static com.shanzha.domain.UserAiHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAiHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAiHistory.class);
        UserAiHistory userAiHistory1 = getUserAiHistorySample1();
        UserAiHistory userAiHistory2 = new UserAiHistory();
        assertThat(userAiHistory1).isNotEqualTo(userAiHistory2);

        userAiHistory2.setId(userAiHistory1.getId());
        assertThat(userAiHistory1).isEqualTo(userAiHistory2);

        userAiHistory2 = getUserAiHistorySample2();
        assertThat(userAiHistory1).isNotEqualTo(userAiHistory2);
    }
}
