package com.shanzha.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.ChapterContent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterContentDTO implements Serializable {

    private Long id;

    @NotNull
    private Long novelId;

    @NotNull
    private Long pageId;

    @Lob
    private byte[] compressed;

    private String compressedContentType;

    private Integer compressionType;

    private String hash;

    @NotNull
    private Instant createTime;

    private ChapterDTO chapter;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNovelId() {
        return novelId;
    }

    public void setNovelId(Long novelId) {
        this.novelId = novelId;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public byte[] getCompressed() {
        return compressed;
    }

    public void setCompressed(byte[] compressed) {
        this.compressed = compressed;
    }

    public String getCompressedContentType() {
        return compressedContentType;
    }

    public void setCompressedContentType(String compressedContentType) {
        this.compressedContentType = compressedContentType;
    }

    public Integer getCompressionType() {
        return compressionType;
    }

    public void setCompressionType(Integer compressionType) {
        this.compressionType = compressionType;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterContentDTO)) {
            return false;
        }

        ChapterContentDTO chapterContentDTO = (ChapterContentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chapterContentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterContentDTO{" +
            "id=" + getId() +
            ", novelId=" + getNovelId() +
            ", pageId=" + getPageId() +
            ", compressed='" + getCompressed() + "'" +
            ", compressionType=" + getCompressionType() +
            ", hash='" + getHash() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", chapter=" + getChapter() +
            "}";
    }
}
