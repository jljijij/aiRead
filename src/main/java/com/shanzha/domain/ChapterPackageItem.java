package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChapterPackageItem.
 */
@Entity
@Table(name = "chapter_package_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterPackageItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @NotNull
    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChapterPackageItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return this.packageId;
    }

    public ChapterPackageItem packageId(Long packageId) {
        this.setPackageId(packageId);
        return this;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getChapterId() {
        return this.chapterId;
    }

    public ChapterPackageItem chapterId(Long chapterId) {
        this.setChapterId(chapterId);
        return this;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterPackageItem)) {
            return false;
        }
        return getId() != null && getId().equals(((ChapterPackageItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterPackageItem{" +
            "id=" + getId() +
            ", packageId=" + getPackageId() +
            ", chapterId=" + getChapterId() +
            "}";
    }
}
