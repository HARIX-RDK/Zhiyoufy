package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;
import java.util.Date;

public class PmsJobTemplate implements Serializable {
    private Long id;

    private Long projectId;

    private Long folderId;

    private String name;

    private String description;

    private String jobPath;

    private String workerLabels;

    private Integer timeoutSeconds;

    private String baseConfPath;

    private String privateConfPath;

    private String configSingles;

    private String configCollections;

    private String extraArgs;

    private Date createdTime;

    private String createdBy;

    private Date modifiedTime;

    private String modifiedBy;

    private Boolean isPerf;

    private String dashboardAddr;

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

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobPath() {
        return jobPath;
    }

    public void setJobPath(String jobPath) {
        this.jobPath = jobPath;
    }

    public String getWorkerLabels() {
        return workerLabels;
    }

    public void setWorkerLabels(String workerLabels) {
        this.workerLabels = workerLabels;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getBaseConfPath() {
        return baseConfPath;
    }

    public void setBaseConfPath(String baseConfPath) {
        this.baseConfPath = baseConfPath;
    }

    public String getPrivateConfPath() {
        return privateConfPath;
    }

    public void setPrivateConfPath(String privateConfPath) {
        this.privateConfPath = privateConfPath;
    }

    public String getConfigSingles() {
        return configSingles;
    }

    public void setConfigSingles(String configSingles) {
        this.configSingles = configSingles;
    }

    public String getConfigCollections() {
        return configCollections;
    }

    public void setConfigCollections(String configCollections) {
        this.configCollections = configCollections;
    }

    public String getExtraArgs() {
        return extraArgs;
    }

    public void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs;
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

    public Boolean getIsPerf() {
        return isPerf;
    }

    public void setIsPerf(Boolean isPerf) {
        this.isPerf = isPerf;
    }

    public String getDashboardAddr() {
        return dashboardAddr;
    }

    public void setDashboardAddr(String dashboardAddr) {
        this.dashboardAddr = dashboardAddr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", folderId=").append(folderId);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", jobPath=").append(jobPath);
        sb.append(", workerLabels=").append(workerLabels);
        sb.append(", timeoutSeconds=").append(timeoutSeconds);
        sb.append(", baseConfPath=").append(baseConfPath);
        sb.append(", privateConfPath=").append(privateConfPath);
        sb.append(", configSingles=").append(configSingles);
        sb.append(", configCollections=").append(configCollections);
        sb.append(", extraArgs=").append(extraArgs);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", modifiedBy=").append(modifiedBy);
        sb.append(", isPerf=").append(isPerf);
        sb.append(", dashboardAddr=").append(dashboardAddr);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}