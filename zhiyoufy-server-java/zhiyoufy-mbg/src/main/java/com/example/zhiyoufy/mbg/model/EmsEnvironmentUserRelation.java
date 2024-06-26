package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;

public class EmsEnvironmentUserRelation implements Serializable {
    private Long id;

    private Long environmentId;

    private Long userId;

    private Boolean isOwner;

    private Boolean isEditor;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public Boolean getIsEditor() {
        return isEditor;
    }

    public void setIsEditor(Boolean isEditor) {
        this.isEditor = isEditor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", environmentId=").append(environmentId);
        sb.append(", userId=").append(userId);
        sb.append(", isOwner=").append(isOwner);
        sb.append(", isEditor=").append(isEditor);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}