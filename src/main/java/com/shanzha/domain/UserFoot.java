package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 用户足迹表
 * 记录用户的点赞、评论、收藏、浏览等行为
 */
@Entity
@Table(name = "user_foot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserFoot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_user_id")
    private Long documentUserId;

    @NotNull
    @Column(name = "document_type", nullable = false)
    private Integer documentType;

    @Column(name = "read_stat")
    private Integer readStat;

    @Column(name = "praise_stat")
    private Integer praiseStat;

    @Column(name = "collection_stat")
    private Integer collectionStat;

    @Column(name = "comment_stat")
    private Integer commentStat;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserFoot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public UserFoot userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDocumentId() {
        return this.documentId;
    }

    public UserFoot documentId(Long documentId) {
        this.setDocumentId(documentId);
        return this;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentUserId() {
        return this.documentUserId;
    }

    public UserFoot documentUserId(Long documentUserId) {
        this.setDocumentUserId(documentUserId);
        return this;
    }

    public void setDocumentUserId(Long documentUserId) {
        this.documentUserId = documentUserId;
    }

    public Integer getDocumentType() {
        return this.documentType;
    }

    public UserFoot documentType(Integer documentType) {
        this.setDocumentType(documentType);
        return this;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public Integer getReadStat() {
        return this.readStat;
    }

    public UserFoot readStat(Integer readStat) {
        this.setReadStat(readStat);
        return this;
    }

    public void setReadStat(Integer readStat) {
        this.readStat = readStat;
    }

    public Integer getPraiseStat() {
        return this.praiseStat;
    }

    public UserFoot praiseStat(Integer praiseStat) {
        this.setPraiseStat(praiseStat);
        return this;
    }

    public void setPraiseStat(Integer praiseStat) {
        this.praiseStat = praiseStat;
    }

    public Integer getCollectionStat() {
        return this.collectionStat;
    }

    public UserFoot collectionStat(Integer collectionStat) {
        this.setCollectionStat(collectionStat);
        return this;
    }

    public void setCollectionStat(Integer collectionStat) {
        this.collectionStat = collectionStat;
    }

    public Integer getCommentStat() {
        return this.commentStat;
    }

    public UserFoot commentStat(Integer commentStat) {
        this.setCommentStat(commentStat);
        return this;
    }

    public void setCommentStat(Integer commentStat) {
        this.commentStat = commentStat;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public UserFoot createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public UserFoot updateTime(Instant updateTime) {
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
        if (!(o instanceof UserFoot)) {
            return false;
        }
        return getId() != null && getId().equals(((UserFoot) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserFoot{" +
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
