package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;
import java.util.Date;

public class EmsConfigItem implements Serializable {
    private Long id;

    private Long environmentId;

    private Long collectionId;

    private String name;

    private String tags;

    private Integer sort;

    private Boolean disabled;

    private Boolean inUse;

    private String usageId;

    private Date usageTimeoutAt;

    private String configValue;

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

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public String getUsageId() {
        return usageId;
    }

    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    public Date getUsageTimeoutAt() {
        return usageTimeoutAt;
    }

    public void setUsageTimeoutAt(Date usageTimeoutAt) {
        this.usageTimeoutAt = usageTimeoutAt;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", environmentId=").append(environmentId);
        sb.append(", collectionId=").append(collectionId);
        sb.append(", name=").append(name);
        sb.append(", tags=").append(tags);
        sb.append(", sort=").append(sort);
        sb.append(", disabled=").append(disabled);
        sb.append(", inUse=").append(inUse);
        sb.append(", usageId=").append(usageId);
        sb.append(", usageTimeoutAt=").append(usageTimeoutAt);
        sb.append(", configValue=").append(configValue);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}