package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Chapter.
 */
@Entity
@Table(name = "chapter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "chapter_no", nullable = false)
    private Integer chapterNo;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "is_vip")
    private Boolean isVip;

    @Column(name = "price")
    private Integer price;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Chapter id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getChapterNo() {
        return this.chapterNo;
    }

    public Chapter chapterNo(Integer chapterNo) {
        this.setChapterNo(chapterNo);
        return this;
    }

    public void setChapterNo(Integer chapterNo) {
        this.chapterNo = chapterNo;
    }

    public String getTitle() {
        return this.title;
    }

    public Chapter title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public Chapter contentId(Long contentId) {
        this.setContentId(contentId);
        return this;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Integer getWordCount() {
        return this.wordCount;
    }

    public Chapter wordCount(Integer wordCount) {
        this.setWordCount(wordCount);
        return this;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Boolean getIsVip() {
        return this.isVip;
    }

    public Chapter isVip(Boolean isVip) {
        this.setIsVip(isVip);
        return this;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Integer getPrice() {
        return this.price;
    }

    public Chapter price(Integer price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public Chapter createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public Chapter updateTime(Instant updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public Novel getNovel() {
        return this.novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    public Chapter novel(Novel novel) {
        this.setNovel(novel);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chapter)) {
            return false;
        }
        return getId() != null && getId().equals(((Chapter) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Chapter{" +
            "id=" + getId() +
            ", chapterNo=" + getChapterNo() +
            ", title='" + getTitle() + "'" +
            ", contentId=" + getContentId() +
            ", wordCount=" + getWordCount() +
            ", isVip='" + getIsVip() + "'" +
            ", price=" + getPrice() +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
