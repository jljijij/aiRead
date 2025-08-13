package com.shanzha.domain;

import static com.shanzha.domain.UserCouponTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.shanzha.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserCouponTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserCoupon.class);
        UserCoupon userCoupon1 = getUserCouponSample1();
        UserCoupon userCoupon2 = new UserCoupon();
        assertThat(userCoupon1).isNotEqualTo(userCoupon2);

        userCoupon2.setId(userCoupon1.getId());
        assertThat(userCoupon1).isEqualTo(userCoupon2);

        userCoupon2 = getUserCouponSample2();
        assertThat(userCoupon1).isNotEqualTo(userCoupon2);
    }
}
