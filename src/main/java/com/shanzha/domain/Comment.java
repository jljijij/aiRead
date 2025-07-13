package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Comment.
 */
@Entity
@Table(name = "comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "article_id", nullable = false)
    private Long articleId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "top_comment_id")
    private Long topCommentId;

    @Column(name = "deleted")
    private Integer deleted;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Comment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return this.articleId;
    }

    public Comment articleId(Long articleId) {
        this.setArticleId(articleId);
        return this;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Comment userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return this.content;
    }

    public Comment content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return this.parentCommentId;
    }

    public Comment parentCommentId(Long parentCommentId) {
        this.setParentCommentId(parentCommentId);
        return this;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Long getTopCommentId() {
        return this.topCommentId;
    }

    public Comment topCommentId(Long topCommentId) {
        this.setTopCommentId(topCommentId);
        return this;
    }

    public void setTopCommentId(Long topCommentId) {
        this.topCommentId = topCommentId;
    }

    public Integer getDeleted() {
        return this.deleted;
    }

    public Comment deleted(Integer deleted) {
        this.setDeleted(deleted);
        return this;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        return getId() != null && getId().equals(((Comment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comment{" +
            "id=" + getId() +
            ", articleId=" + getArticleId() +
            ", userId=" + getUserId() +
            ", content='" + getContent() + "'" +
            ", parentCommentId=" + getParentCommentId() +
            ", topCommentId=" + getTopCommentId() +
            ", deleted=" + getDeleted() +
            "}";
    }
}
