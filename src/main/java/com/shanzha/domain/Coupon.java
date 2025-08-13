package com.shanzha.domain;

import com.shanzha.domain.enumeration.CouponType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Coupon.
 */
@Entity
@Table(name = "coupon")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull
    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponType type;

    @NotNull
    @Column(name = "novel_id", nullable = false)
    private Long novelId;

    @Column(name = "used")
    private Boolean used;

    @Column(name = "chapter_id")
    private Long chapterId;

    @Column(name = "package_id")
    private Long packageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Coupon id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Coupon code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getExpiredAt() {
        return this.expiredAt;
    }

    public Coupon expiredAt(Instant expiredAt) {
        this.setExpiredAt(expiredAt);
        return this;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public CouponType getType() {
        return this.type;
    }

    public Coupon type(CouponType type) {
        this.setType(type);
        return this;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public Long getNovelId() {
        return this.novelId;
    }

    public Coupon novelId(Long novelId) {
        this.setNovelId(novelId);
        return this;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Boolean getUsed() {
        return this.used;
    }

    public Coupon used(Boolean used) {
        this.setUsed(used);
        return this;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public Long getChapterId() {
        return this.chapterId;
    }

    public Coupon chapterId(Long chapterId) {
        this.setChapterId(chapterId);
        return this;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getPackageId() {
        return this.packageId;
    }

    public Coupon packageId(Long packageId) {
        this.setPackageId(packageId);
        return this;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon)) {
            return false;
        }
        return getId() != null && getId().equals(((Coupon) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Coupon{" +
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
