package com.shanzha.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChapterContent.
 */
@Entity
@Table(name = "chapter_content")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "novel_id", nullable = false)
    private Long novelId;

    @NotNull
    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Lob
    @Column(name = "compressed", nullable = false)
    private byte[] compressed;

    @NotNull
    @Column(name = "compressed_content_type", nullable = false)
    private String compressedContentType;

    @Column(name = "compression_type")
    private Integer compressionType;

    @Column(name = "hash")
    private String hash;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "novel" }, allowSetters = true)
    private Chapter chapter;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChapterContent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNovelId() {
        return this.novelId;
    }

    public ChapterContent novelId(Long novelId) {
        this.setNovelId(novelId);
        return this;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public ChapterContent pageId(Long pageId) {
        this.setPageId(pageId);
        return this;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public byte[] getCompressed() {
        return this.compressed;
    }

    public ChapterContent compressed(byte[] compressed) {
        this.setCompressed(compressed);
        return this;
    }

    public void setCompressed(byte[] compressed) {
        this.compressed = compressed;
    }

    public String getCompressedContentType() {
        return this.compressedContentType;
    }

    public ChapterContent compressedContentType(String compressedContentType) {
        this.compressedContentType = compressedContentType;
        return this;
    }

    public void setCompressedContentType(String compressedContentType) {
        this.compressedContentType = compressedContentType;
    }

    public Integer getCompressionType() {
        return this.compressionType;
    }

    public ChapterContent compressionType(Integer compressionType) {
        this.setCompressionType(compressionType);
        return this;
    }

    public void setCompressionType(Integer compressionType) {
        this.compressionType = compressionType;
    }

    public String getHash() {
        return this.hash;
    }

    public ChapterContent hash(String hash) {
        this.setHash(hash);
        return this;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public ChapterContent createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Chapter getChapter() {
        return this.chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public ChapterContent chapter(Chapter chapter) {
        this.setChapter(chapter);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterContent)) {
            return false;
        }
        return getId() != null && getId().equals(((ChapterContent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterContent{" +
            "id=" + getId() +
            ", novelId=" + getNovelId() +
            ", pageId=" + getPageId() +
            ", compressed='" + getCompressed() + "'" +
            ", compressedContentType='" + getCompressedContentType() + "'" +
            ", compressionType=" + getCompressionType() +
            ", hash='" + getHash() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
