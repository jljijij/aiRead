package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NovelTag.
 */
@Entity
@Table(name = "novel_tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NovelTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tag_id", nullable = false)
    private String tagId;

    @NotNull
    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @NotNull
    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "is_hot")
    private Boolean isHot;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NovelTag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagId() {
        return this.tagId;
    }

    public NovelTag tagId(String tagId) {
        this.setTagId(tagId);
        return this;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public NovelTag tagName(String tagName) {
        this.setTagName(tagName);
        return this;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCategory() {
        return this.category;
    }

    public NovelTag category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsHot() {
        return this.isHot;
    }

    public NovelTag isHot(Boolean isHot) {
        this.setIsHot(isHot);
        return this;
    }

    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public NovelTag createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public NovelTag updateTime(Instant updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NovelTag)) {
            return false;
        }
        return getId() != null && getId().equals(((NovelTag) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NovelTag{" +
            "id=" + getId() +
            ", tagId='" + getTagId() + "'" +
            ", tagName='" + getTagName() + "'" +
            ", category='" + getCategory() + "'" +
            ", isHot='" + getIsHot() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
