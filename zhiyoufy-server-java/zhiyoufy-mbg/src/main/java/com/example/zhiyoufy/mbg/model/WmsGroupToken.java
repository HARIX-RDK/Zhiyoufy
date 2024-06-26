package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;
import java.util.Date;

public class WmsGroupToken implements Serializable {
    private Long id;

    private Long workerAppId;

    private Long workerGroupId;

    private String name;

    private String secret;

    private Date expiryTime;

    private String description;

    private Date createdTime;

    private String createdBy;

    private Date modifiedTime;

    private String modifiedBy;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkerAppId() {
        return workerAppId;
    }

    public void setWorkerAppId(Long workerAppId) {
        this.workerAppId = workerAppId;
    }

    public Long getWorkerGroupId() {
        return workerGroupId;
    }

    public void setWorkerGroupId(Long workerGroupId) {
        this.workerGroupId = workerGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", workerAppId=").append(workerAppId);
        sb.append(", workerGroupId=").append(workerGroupId);
        sb.append(", name=").append(name);
        sb.append(", secret=").append(secret);
        sb.append(", expiryTime=").append(expiryTime);
        sb.append(", description=").append(description);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", modifiedBy=").append(modifiedBy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}