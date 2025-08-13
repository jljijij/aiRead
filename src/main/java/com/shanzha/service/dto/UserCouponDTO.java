package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.UserCoupon} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserCouponDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long couponId;

    private Instant usedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCouponDTO)) {
            return false;
        }

        UserCouponDTO userCouponDTO = (UserCouponDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userCouponDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserCouponDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", couponId=" + getCouponId() +
            ", usedAt='" + getUsedAt() + "'" +
            "}";
    }
}
