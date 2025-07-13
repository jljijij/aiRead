package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAiHistoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAiHistoryDTO.class);
        UserAiHistoryDTO userAiHistoryDTO1 = new UserAiHistoryDTO();
        userAiHistoryDTO1.setId(1L);
        UserAiHistoryDTO userAiHistoryDTO2 = new UserAiHistoryDTO();
        assertThat(userAiHistoryDTO1).isNotEqualTo(userAiHistoryDTO2);
        userAiHistoryDTO2.setId(userAiHistoryDTO1.getId());
        assertThat(userAiHistoryDTO1).isEqualTo(userAiHistoryDTO2);
        userAiHistoryDTO2.setId(2L);
        assertThat(userAiHistoryDTO1).isNotEqualTo(userAiHistoryDTO2);
        userAiHistoryDTO1.setId(null);
        assertThat(userAiHistoryDTO1).isNotEqualTo(userAiHistoryDTO2);
    }
}
