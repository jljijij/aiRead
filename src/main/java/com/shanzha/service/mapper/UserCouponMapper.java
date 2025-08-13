package com.shanzha.service.mapper;

import com.shanzha.domain.UserCoupon;
import com.shanzha.service.dto.UserCouponDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserCoupon} and its DTO {@link UserCouponDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserCouponMapper extends EntityMapper<UserCouponDTO, UserCoupon> {}
