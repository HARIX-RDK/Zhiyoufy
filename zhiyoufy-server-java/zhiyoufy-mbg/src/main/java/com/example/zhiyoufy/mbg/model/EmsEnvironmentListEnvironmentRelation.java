package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;

public class EmsEnvironmentListEnvironmentRelation implements Serializable {
    private Long id;

    private Long environmentListId;

    private Long environmentId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnvironmentListId() {
        return environmentListId;
    }

    public void setEnvironmentListId(Long environmentListId) {
        this.environmentListId = environmentListId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", environmentListId=").append(environmentListId);
        sb.append(", environmentId=").append(environmentId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}