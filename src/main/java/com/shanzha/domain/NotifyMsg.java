package com.shanzha.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A NotifyMsg.
 */
@Entity
@Table(name = "notify_msg")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotifyMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "related_id", nullable = false)
    private String relatedId;

    @Column(name = "related_info")
    private String relatedInfo;

    @NotNull
    @Column(name = "operate_user_id", nullable = false)
    private Long operateUserId;

    @Column(name = "operate_user_name")
    private String operateUserName;

    @Column(name = "operate_user_photo")
    private String operateUserPhoto;

    @NotNull
    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "msg")
    private String msg;

    @NotNull
    @Column(name = "state", nullable = false)
    private Integer state;

    @Column(name = "create_time")
    private Instant createTime;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotifyMsg id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelatedId() {
        return this.relatedId;
    }

    public NotifyMsg relatedId(String relatedId) {
        this.setRelatedId(relatedId);
        return this;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getRelatedInfo() {
        return this.relatedInfo;
    }

    public NotifyMsg relatedInfo(String relatedInfo) {
        this.setRelatedInfo(relatedInfo);
        return this;
    }

    public void setRelatedInfo(String relatedInfo) {
        this.relatedInfo = relatedInfo;
    }

    public Long getOperateUserId() {
        return this.operateUserId;
    }

    public NotifyMsg operateUserId(Long operateUserId) {
        this.setOperateUserId(operateUserId);
        return this;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserName() {
        return this.operateUserName;
    }

    public NotifyMsg operateUserName(String operateUserName) {
        this.setOperateUserName(operateUserName);
        return this;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserPhoto() {
        return this.operateUserPhoto;
    }

    public NotifyMsg operateUserPhoto(String operateUserPhoto) {
        this.setOperateUserPhoto(operateUserPhoto);
        return this;
    }

    public void setOperateUserPhoto(String operateUserPhoto) {
        this.operateUserPhoto = operateUserPhoto;
    }

    public Integer getType() {
        return this.type;
    }

    public NotifyMsg type(Integer type) {
        this.setType(type);
        return this;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return this.msg;
    }

    public NotifyMsg msg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getState() {
        return this.state;
    }

    public NotifyMsg state(Integer state) {
        this.setState(state);
        return this;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public NotifyMsg createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotifyMsg)) {
            return false;
        }
        return getId() != null && getId().equals(((NotifyMsg) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotifyMsg{" +
            "id=" + getId() +
            ", relatedId='" + getRelatedId() + "'" +
            ", relatedInfo='" + getRelatedInfo() + "'" +
            ", operateUserId=" + getOperateUserId() +
            ", operateUserName='" + getOperateUserName() + "'" +
            ", operateUserPhoto='" + getOperateUserPhoto() + "'" +
            ", type=" + getType() +
            ", msg='" + getMsg() + "'" +
            ", state=" + getState() +
            ", createTime='" + getCreateTime() + "'" +
            "}";
    }
}
