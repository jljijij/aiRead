package com.shanzha.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.NotifyMsg} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotifyMsgDTO implements Serializable {

    private Long id;

    @NotNull
    private String relatedId;

    private String relatedInfo;

    @NotNull
    private Long operateUserId;

    private String operateUserName;

    private String operateUserPhoto;

    @NotNull
    private Integer type;

    private String msg;

    @NotNull
    private Integer state;

    private Instant createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getRelatedInfo() {
        return relatedInfo;
    }

    public void setRelatedInfo(String relatedInfo) {
        this.relatedInfo = relatedInfo;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserPhoto() {
        return operateUserPhoto;
    }

    public void setOperateUserPhoto(String operateUserPhoto) {
        this.operateUserPhoto = operateUserPhoto;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotifyMsgDTO)) {
            return false;
        }

        NotifyMsgDTO notifyMsgDTO = (NotifyMsgDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notifyMsgDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotifyMsgDTO{" +
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
