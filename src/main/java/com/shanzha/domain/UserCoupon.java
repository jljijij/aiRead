package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserCoupon.
 */
@Entity
@Table(name = "user_coupon")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "used_at")
    private Instant usedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserCoupon id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public UserCoupon userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCouponId() {
        return this.couponId;
    }

    public UserCoupon couponId(Long couponId) {
        this.setCouponId(couponId);
        return this;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Instant getUsedAt() {
        return this.usedAt;
    }

    public UserCoupon usedAt(Instant usedAt) {
        this.setUsedAt(usedAt);
        return this;
    }

    public void setUsedAt(Instant usedAt) {
        this.usedAt = usedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserCoupon)) {
            return false;
        }
        return getId() != null && getId().equals(((UserCoupon) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserCoupon{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", couponId=" + getCouponId() +
            ", usedAt='" + getUsedAt() + "'" +
            "}";
    }
}
