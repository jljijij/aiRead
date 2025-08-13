package com.shanzha.service.mapper;

import static com.shanzha.domain.UserCouponAsserts.*;
import static com.shanzha.domain.UserCouponTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserCouponMapperTest {

    private UserCouponMapper userCouponMapper;

    @BeforeEach
    void setUp() {
        userCouponMapper = new UserCouponMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserCouponSample1();
        var actual = userCouponMapper.toEntity(userCouponMapper.toDto(expected));
        assertUserCouponAllPropertiesEquals(expected, actual);
    }
}
