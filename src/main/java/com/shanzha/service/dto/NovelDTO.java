package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.Novel} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NovelDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Long authorId;

    private String coverUrl;

    private String description;

    @NotNull
    private Integer categoryId;

    private String tags;

    private Integer wordCount;

    private Integer chapterCount;

    @NotNull
    private Integer status;

    private Boolean isVip;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
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
        if (!(o instanceof NovelDTO)) {
            return false;
        }

        NovelDTO novelDTO = (NovelDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, novelDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NovelDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", authorId=" + getAuthorId() +
            ", coverUrl='" + getCoverUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", categoryId=" + getCategoryId() +
            ", tags='" + getTags() + "'" +
            ", wordCount=" + getWordCount() +
            ", chapterCount=" + getChapterCount() +
            ", status=" + getStatus() +
            ", isVip='" + getIsVip() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
