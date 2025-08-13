package com.shanzha.service.dto;

import com.shanzha.domain.enumeration.CouponType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.Coupon} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CouponDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private Instant expiredAt;

    @NotNull
    private CouponType type;

    @NotNull
    private Long novelId;

    private Boolean used;

    private Long chapterId;

    private Long packageId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouponDTO)) {
            return false;
        }

        CouponDTO couponDTO = (CouponDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, couponDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CouponDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", expiredAt='" + getExpiredAt() + "'" +
            ", type='" + getType() + "'" +
            ", novelId=" + getNovelId() +
            ", used='" + getUsed() + "'" +
            ", chapterId=" + getChapterId() +
            ", packageId=" + getPackageId() +
            "}";
    }
}
