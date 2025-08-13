package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.NovelTag} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NovelTagDTO implements Serializable {

    private Long id;

    @NotNull
    private String tagId;

    @NotNull
    private String tagName;

    @NotNull
    private String category;

    private Boolean isHot;

    @NotNull
    private Instant createTime;

    @NotNull
    private Instant updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsHot() {
        return isHot;
    }

    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NovelTagDTO)) {
            return false;
        }

        NovelTagDTO novelTagDTO = (NovelTagDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, novelTagDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NovelTagDTO{" +
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
