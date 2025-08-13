package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Novel.
 */
@Entity
@Table(name = "novel")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Novel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "tags")
    private String tags;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "chapter_count")
    private Integer chapterCount;

    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "is_vip")
    private Boolean isVip;

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

    public Novel id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Novel title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return this.authorId;
    }

    public Novel authorId(Long authorId) {
        this.setAuthorId(authorId);
        return this;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getCoverUrl() {
        return this.coverUrl;
    }

    public Novel coverUrl(String coverUrl) {
        this.setCoverUrl(coverUrl);
        return this;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public Novel description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public Novel categoryId(Integer categoryId) {
        this.setCategoryId(categoryId);
        return this;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getTags() {
        return this.tags;
    }

    public Novel tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWordCount() {
        return this.wordCount;
    }

    public Novel wordCount(Integer wordCount) {
        this.setWordCount(wordCount);
        return this;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getChapterCount() {
        return this.chapterCount;
    }

    public Novel chapterCount(Integer chapterCount) {
        this.setChapterCount(chapterCount);
        return this;
    }

    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Novel status(Integer status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsVip() {
        return this.isVip;
    }

    public Novel isVip(Boolean isVip) {
        this.setIsVip(isVip);
        return this;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public Novel createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public Novel updateTime(Instant updateTime) {
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
        if (!(o instanceof Novel)) {
            return false;
        }
        return getId() != null && getId().equals(((Novel) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Novel{" +
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
