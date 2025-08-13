package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.ChapterPermission} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterPermissionDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long chapterId;

    @NotNull
    private Boolean canRead;

    private Boolean canDownload;

    private Boolean canComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanDownload() {
        return canDownload;
    }

    public void setCanDownload(Boolean canDownload) {
        this.canDownload = canDownload;
    }

    public Boolean getCanComment() {
        return canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterPermissionDTO)) {
            return false;
        }

        ChapterPermissionDTO chapterPermissionDTO = (ChapterPermissionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chapterPermissionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterPermissionDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", chapterId=" + getChapterId() +
            ", canRead='" + getCanRead() + "'" +
            ", canDownload='" + getCanDownload() + "'" +
            ", canComment='" + getCanComment() + "'" +
            "}";
    }
}
