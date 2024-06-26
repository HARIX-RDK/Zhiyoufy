package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;

public class PmsProjectUserRelation implements Serializable {
    private Long id;

    private Long projectId;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
        sb.append(", projectId=").append(projectId);
        sb.append(", userId=").append(userId);
        sb.append(", isOwner=").append(isOwner);
        sb.append(", isEditor=").append(isEditor);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}