package com.shanzha.service.mapper;

import com.shanzha.domain.Coupon;
import com.shanzha.service.dto.CouponDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Coupon} and its DTO {@link CouponDTO}.
 */
@Mapper(componentModel = "spring")
public interface CouponMapper extends EntityMapper<CouponDTO, Coupon> {}
