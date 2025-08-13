package com.shanzha.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserCouponDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserCouponDTO.class);
        UserCouponDTO userCouponDTO1 = new UserCouponDTO();
        userCouponDTO1.setId(1L);
        UserCouponDTO userCouponDTO2 = new UserCouponDTO();
        assertThat(userCouponDTO1).isNotEqualTo(userCouponDTO2);
        userCouponDTO2.setId(userCouponDTO1.getId());
        assertThat(userCouponDTO1).isEqualTo(userCouponDTO2);
        userCouponDTO2.setId(2L);
        assertThat(userCouponDTO1).isNotEqualTo(userCouponDTO2);
        userCouponDTO1.setId(null);
        assertThat(userCouponDTO1).isNotEqualTo(userCouponDTO2);
    }
}
