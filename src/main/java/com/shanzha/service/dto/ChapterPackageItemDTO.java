package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.ChapterPackageItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterPackageItemDTO implements Serializable {

    private Long id;

    @NotNull
    private Long packageId;

    @NotNull
    private Long chapterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterPackageItemDTO)) {
            return false;
        }

        ChapterPackageItemDTO chapterPackageItemDTO = (ChapterPackageItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chapterPackageItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterPackageItemDTO{" +
            "id=" + getId() +
            ", packageId=" + getPackageId() +
            ", chapterId=" + getChapterId() +
            "}";
    }
}
