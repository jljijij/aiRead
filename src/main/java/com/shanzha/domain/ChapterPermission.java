package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ChapterPermission.
 */
@Entity
@Table(name = "chapter_permission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChapterPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @NotNull
    @Column(name = "can_read", nullable = false)
    private Boolean canRead;

    @Column(name = "can_download")
    private Boolean canDownload;

    @Column(name = "can_comment")
    private Boolean canComment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ChapterPermission id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public ChapterPermission userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChapterId() {
        return this.chapterId;
    }

    public ChapterPermission chapterId(Long chapterId) {
        this.setChapterId(chapterId);
        return this;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Boolean getCanRead() {
        return this.canRead;
    }

    public ChapterPermission canRead(Boolean canRead) {
        this.setCanRead(canRead);
        return this;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanDownload() {
        return this.canDownload;
    }

    public ChapterPermission canDownload(Boolean canDownload) {
        this.setCanDownload(canDownload);
        return this;
    }

    public void setCanDownload(Boolean canDownload) {
        this.canDownload = canDownload;
    }

    public Boolean getCanComment() {
        return this.canComment;
    }

    public ChapterPermission canComment(Boolean canComment) {
        this.setCanComment(canComment);
        return this;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChapterPermission)) {
            return false;
        }
        return getId() != null && getId().equals(((ChapterPermission) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChapterPermission{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", chapterId=" + getChapterId() +
            ", canRead='" + getCanRead() + "'" +
            ", canDownload='" + getCanDownload() + "'" +
            ", canComment='" + getCanComment() + "'" +
            "}";
    }
}
