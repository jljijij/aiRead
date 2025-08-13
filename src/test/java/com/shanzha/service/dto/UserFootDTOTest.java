package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserFootDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserFootDTO.class);
        UserFootDTO userFootDTO1 = new UserFootDTO();
        userFootDTO1.setId(1L);
        UserFootDTO userFootDTO2 = new UserFootDTO();
        assertThat(userFootDTO1).isNotEqualTo(userFootDTO2);
        userFootDTO2.setId(userFootDTO1.getId());
        assertThat(userFootDTO1).isEqualTo(userFootDTO2);
        userFootDTO2.setId(2L);
        assertThat(userFootDTO1).isNotEqualTo(userFootDTO2);
        userFootDTO1.setId(null);
        assertThat(userFootDTO1).isNotEqualTo(userFootDTO2);
    }
}
