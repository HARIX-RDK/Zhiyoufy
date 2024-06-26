package com.example.zhiyoufy.mbg.model;

import java.io.Serializable;
import java.util.Date;

public class JmsJobSchedule implements Serializable {
    private Long id;

    private String name;

    private Long projectId;

    private String projectName;

    private Long workerAppId;

    private String workerAppName;

    private Long workerGroupId;

    private String workerGroupName;

    private Long environmentId;

    private String environmentName;

    private Long templateId;

    private String templateName;

    private String runTag;

    private Integer runNum;

    private Integer parallelNum;

    private String includeTags;

    private String excludeTags;

    private String addTags;

    private String removeTags;

    private String crontabConfig;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getWorkerAppId() {
        return workerAppId;
    }

    public void setWorkerAppId(Long workerAppId) {
        this.workerAppId = workerAppId;
    }

    public String getWorkerAppName() {
        return workerAppName;
    }

    public void setWorkerAppName(String workerAppName) {
        this.workerAppName = workerAppName;
    }

    public Long getWorkerGroupId() {
        return workerGroupId;
    }

    public void setWorkerGroupId(Long workerGroupId) {
        this.workerGroupId = workerGroupId;
    }

    public String getWorkerGroupName() {
        return workerGroupName;
    }

    public void setWorkerGroupName(String workerGroupName) {
        this.workerGroupName = workerGroupName;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getRunTag() {
        return runTag;
    }

    public void setRunTag(String runTag) {
        this.runTag = runTag;
    }

    public Integer getRunNum() {
        return runNum;
    }

    public void setRunNum(Integer runNum) {
        this.runNum = runNum;
    }

    public Integer getParallelNum() {
        return parallelNum;
    }

    public void setParallelNum(Integer parallelNum) {
        this.parallelNum = parallelNum;
    }

    public String getIncludeTags() {
        return includeTags;
    }

    public void setIncludeTags(String includeTags) {
        this.includeTags = includeTags;
    }

    public String getExcludeTags() {
        return excludeTags;
    }

    public void setExcludeTags(String excludeTags) {
        this.excludeTags = excludeTags;
    }

    public String getAddTags() {
        return addTags;
    }

    public void setAddTags(String addTags) {
        this.addTags = addTags;
    }

    public String getRemoveTags() {
        return removeTags;
    }

    public void setRemoveTags(String removeTags) {
        this.removeTags = removeTags;
    }

    public String getCrontabConfig() {
        return crontabConfig;
    }

    public void setCrontabConfig(String crontabConfig) {
        this.crontabConfig = crontabConfig;
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
        sb.append(", name=").append(name);
        sb.append(", projectId=").append(projectId);
        sb.append(", projectName=").append(projectName);
        sb.append(", workerAppId=").append(workerAppId);
        sb.append(", workerAppName=").append(workerAppName);
        sb.append(", workerGroupId=").append(workerGroupId);
        sb.append(", workerGroupName=").append(workerGroupName);
        sb.append(", environmentId=").append(environmentId);
        sb.append(", environmentName=").append(environmentName);
        sb.append(", templateId=").append(templateId);
        sb.append(", templateName=").append(templateName);
        sb.append(", runTag=").append(runTag);
        sb.append(", runNum=").append(runNum);
        sb.append(", parallelNum=").append(parallelNum);
        sb.append(", includeTags=").append(includeTags);
        sb.append(", excludeTags=").append(excludeTags);
        sb.append(", addTags=").append(addTags);
        sb.append(", removeTags=").append(removeTags);
        sb.append(", crontabConfig=").append(crontabConfig);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append(", modifiedBy=").append(modifiedBy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}