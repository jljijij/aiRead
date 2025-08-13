package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.Chapter} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer chapterNo;

    @NotNull
    private String title;

    @NotNull
    private Long contentId;

    private Integer wordCount;

    private Boolean isVip;

    private Integer price;

    @NotNull
    private Instant createTime;

    @NotNull
    private Instant updateTime;

    private NovelDTO novel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(Integer chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public NovelDTO getNovel() {
        return novel;
    }

    public void setNovel(NovelDTO novel) {
        this.novel = novel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterDTO)) {
            return false;
        }

        ChapterDTO chapterDTO = (ChapterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chapterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterDTO{" +
            "id=" + getId() +
            ", chapterNo=" + getChapterNo() +
            ", title='" + getTitle() + "'" +
            ", contentId=" + getContentId() +
            ", wordCount=" + getWordCount() +
            ", isVip='" + getIsVip() + "'" +
            ", price=" + getPrice() +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            ", novel=" + getNovel() +
            "}";
    }
}
