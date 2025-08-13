package com.shanzha.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.UserFoot} entity.
 */
@Schema(description = "用户足迹表\n记录用户的点赞、评论、收藏、浏览等行为")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserFootDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long documentId;

    private Long documentUserId;

    @NotNull
    private Integer documentType;

    private Integer readStat;

    private Integer praiseStat;

    private Integer collectionStat;

    private Integer commentStat;

    private Instant createTime;

    private Instant updateTime;

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

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentUserId() {
        return documentUserId;
    }

    public void setDocumentUserId(Long documentUserId) {
        this.documentUserId = documentUserId;
    }

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public Integer getReadStat() {
        return readStat;
    }

    public void setReadStat(Integer readStat) {
        this.readStat = readStat;
    }

    public Integer getPraiseStat() {
        return praiseStat;
    }

    public void setPraiseStat(Integer praiseStat) {
        this.praiseStat = praiseStat;
    }

    public Integer getCollectionStat() {
        return collectionStat;
    }

    public void setCollectionStat(Integer collectionStat) {
        this.collectionStat = collectionStat;
    }

    public Integer getCommentStat() {
        return commentStat;
    }

    public void setCommentStat(Integer commentStat) {
        this.commentStat = commentStat;
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
        if (!(o instanceof UserFootDTO)) {
            return false;
        }

        UserFootDTO userFootDTO = (UserFootDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userFootDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserFootDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", documentId=" + getDocumentId() +
            ", documentUserId=" + getDocumentUserId() +
            ", documentType=" + getDocumentType() +
            ", readStat=" + getReadStat() +
            ", praiseStat=" + getPraiseStat() +
            ", collectionStat=" + getCollectionStat() +
            ", commentStat=" + getCommentStat() +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            "}";
    }
}
