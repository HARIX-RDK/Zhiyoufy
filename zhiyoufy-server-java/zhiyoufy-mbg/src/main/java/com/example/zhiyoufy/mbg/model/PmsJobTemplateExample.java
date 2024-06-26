package com.example.zhiyoufy.mbg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsJobTemplateExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public PmsJobTemplateExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNull() {
            addCriterion("project_id is null");
            return (Criteria) this;
        }

        public Criteria andProjectIdIsNotNull() {
            addCriterion("project_id is not null");
            return (Criteria) this;
        }

        public Criteria andProjectIdEqualTo(Long value) {
            addCriterion("project_id =", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotEqualTo(Long value) {
            addCriterion("project_id <>", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThan(Long value) {
            addCriterion("project_id >", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdGreaterThanOrEqualTo(Long value) {
            addCriterion("project_id >=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThan(Long value) {
            addCriterion("project_id <", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdLessThanOrEqualTo(Long value) {
            addCriterion("project_id <=", value, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdIn(List<Long> values) {
            addCriterion("project_id in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotIn(List<Long> values) {
            addCriterion("project_id not in", values, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdBetween(Long value1, Long value2) {
            addCriterion("project_id between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andProjectIdNotBetween(Long value1, Long value2) {
            addCriterion("project_id not between", value1, value2, "projectId");
            return (Criteria) this;
        }

        public Criteria andFolderIdIsNull() {
            addCriterion("folder_id is null");
            return (Criteria) this;
        }

        public Criteria andFolderIdIsNotNull() {
            addCriterion("folder_id is not null");
            return (Criteria) this;
        }

        public Criteria andFolderIdEqualTo(Long value) {
            addCriterion("folder_id =", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdNotEqualTo(Long value) {
            addCriterion("folder_id <>", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdGreaterThan(Long value) {
            addCriterion("folder_id >", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdGreaterThanOrEqualTo(Long value) {
            addCriterion("folder_id >=", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdLessThan(Long value) {
            addCriterion("folder_id <", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdLessThanOrEqualTo(Long value) {
            addCriterion("folder_id <=", value, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdIn(List<Long> values) {
            addCriterion("folder_id in", values, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdNotIn(List<Long> values) {
            addCriterion("folder_id not in", values, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdBetween(Long value1, Long value2) {
            addCriterion("folder_id between", value1, value2, "folderId");
            return (Criteria) this;
        }

        public Criteria andFolderIdNotBetween(Long value1, Long value2) {
            addCriterion("folder_id not between", value1, value2, "folderId");
            return (Criteria) this;
        }

        public Criteria andNameIsNull() {
            addCriterion("name is null");
            return (Criteria) this;
        }

        public Criteria andNameIsNotNull() {
            addCriterion("name is not null");
            return (Criteria) this;
        }

        public Criteria andNameEqualTo(String value) {
            addCriterion("name =", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotEqualTo(String value) {
            addCriterion("name <>", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThan(String value) {
            addCriterion("name >", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameGreaterThanOrEqualTo(String value) {
            addCriterion("name >=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThan(String value) {
            addCriterion("name <", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLessThanOrEqualTo(String value) {
            addCriterion("name <=", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameLike(String value) {
            addCriterion("name like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotLike(String value) {
            addCriterion("name not like", value, "name");
            return (Criteria) this;
        }

        public Criteria andNameIn(List<String> values) {
            addCriterion("name in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotIn(List<String> values) {
            addCriterion("name not in", values, "name");
            return (Criteria) this;
        }

        public Criteria andNameBetween(String value1, String value2) {
            addCriterion("name between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andNameNotBetween(String value1, String value2) {
            addCriterion("name not between", value1, value2, "name");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andJobPathIsNull() {
            addCriterion("job_path is null");
            return (Criteria) this;
        }

        public Criteria andJobPathIsNotNull() {
            addCriterion("job_path is not null");
            return (Criteria) this;
        }

        public Criteria andJobPathEqualTo(String value) {
            addCriterion("job_path =", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathNotEqualTo(String value) {
            addCriterion("job_path <>", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathGreaterThan(String value) {
            addCriterion("job_path >", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathGreaterThanOrEqualTo(String value) {
            addCriterion("job_path >=", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathLessThan(String value) {
            addCriterion("job_path <", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathLessThanOrEqualTo(String value) {
            addCriterion("job_path <=", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathLike(String value) {
            addCriterion("job_path like", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathNotLike(String value) {
            addCriterion("job_path not like", value, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathIn(List<String> values) {
            addCriterion("job_path in", values, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathNotIn(List<String> values) {
            addCriterion("job_path not in", values, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathBetween(String value1, String value2) {
            addCriterion("job_path between", value1, value2, "jobPath");
            return (Criteria) this;
        }

        public Criteria andJobPathNotBetween(String value1, String value2) {
            addCriterion("job_path not between", value1, value2, "jobPath");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsIsNull() {
            addCriterion("worker_labels is null");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsIsNotNull() {
            addCriterion("worker_labels is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsEqualTo(String value) {
            addCriterion("worker_labels =", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsNotEqualTo(String value) {
            addCriterion("worker_labels <>", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsGreaterThan(String value) {
            addCriterion("worker_labels >", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsGreaterThanOrEqualTo(String value) {
            addCriterion("worker_labels >=", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsLessThan(String value) {
            addCriterion("worker_labels <", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsLessThanOrEqualTo(String value) {
            addCriterion("worker_labels <=", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsLike(String value) {
            addCriterion("worker_labels like", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsNotLike(String value) {
            addCriterion("worker_labels not like", value, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsIn(List<String> values) {
            addCriterion("worker_labels in", values, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsNotIn(List<String> values) {
            addCriterion("worker_labels not in", values, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsBetween(String value1, String value2) {
            addCriterion("worker_labels between", value1, value2, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andWorkerLabelsNotBetween(String value1, String value2) {
            addCriterion("worker_labels not between", value1, value2, "workerLabels");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsIsNull() {
            addCriterion("timeout_seconds is null");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsIsNotNull() {
            addCriterion("timeout_seconds is not null");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsEqualTo(Integer value) {
            addCriterion("timeout_seconds =", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsNotEqualTo(Integer value) {
            addCriterion("timeout_seconds <>", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsGreaterThan(Integer value) {
            addCriterion("timeout_seconds >", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsGreaterThanOrEqualTo(Integer value) {
            addCriterion("timeout_seconds >=", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsLessThan(Integer value) {
            addCriterion("timeout_seconds <", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsLessThanOrEqualTo(Integer value) {
            addCriterion("timeout_seconds <=", value, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsIn(List<Integer> values) {
            addCriterion("timeout_seconds in", values, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsNotIn(List<Integer> values) {
            addCriterion("timeout_seconds not in", values, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsBetween(Integer value1, Integer value2) {
            addCriterion("timeout_seconds between", value1, value2, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andTimeoutSecondsNotBetween(Integer value1, Integer value2) {
            addCriterion("timeout_seconds not between", value1, value2, "timeoutSeconds");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathIsNull() {
            addCriterion("base_conf_path is null");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathIsNotNull() {
            addCriterion("base_conf_path is not null");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathEqualTo(String value) {
            addCriterion("base_conf_path =", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathNotEqualTo(String value) {
            addCriterion("base_conf_path <>", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathGreaterThan(String value) {
            addCriterion("base_conf_path >", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathGreaterThanOrEqualTo(String value) {
            addCriterion("base_conf_path >=", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathLessThan(String value) {
            addCriterion("base_conf_path <", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathLessThanOrEqualTo(String value) {
            addCriterion("base_conf_path <=", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathLike(String value) {
            addCriterion("base_conf_path like", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathNotLike(String value) {
            addCriterion("base_conf_path not like", value, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathIn(List<String> values) {
            addCriterion("base_conf_path in", values, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathNotIn(List<String> values) {
            addCriterion("base_conf_path not in", values, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathBetween(String value1, String value2) {
            addCriterion("base_conf_path between", value1, value2, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andBaseConfPathNotBetween(String value1, String value2) {
            addCriterion("base_conf_path not between", value1, value2, "baseConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathIsNull() {
            addCriterion("private_conf_path is null");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathIsNotNull() {
            addCriterion("private_conf_path is not null");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathEqualTo(String value) {
            addCriterion("private_conf_path =", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathNotEqualTo(String value) {
            addCriterion("private_conf_path <>", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathGreaterThan(String value) {
            addCriterion("private_conf_path >", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathGreaterThanOrEqualTo(String value) {
            addCriterion("private_conf_path >=", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathLessThan(String value) {
            addCriterion("private_conf_path <", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathLessThanOrEqualTo(String value) {
            addCriterion("private_conf_path <=", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathLike(String value) {
            addCriterion("private_conf_path like", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathNotLike(String value) {
            addCriterion("private_conf_path not like", value, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathIn(List<String> values) {
            addCriterion("private_conf_path in", values, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathNotIn(List<String> values) {
            addCriterion("private_conf_path not in", values, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathBetween(String value1, String value2) {
            addCriterion("private_conf_path between", value1, value2, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andPrivateConfPathNotBetween(String value1, String value2) {
            addCriterion("private_conf_path not between", value1, value2, "privateConfPath");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesIsNull() {
            addCriterion("config_singles is null");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesIsNotNull() {
            addCriterion("config_singles is not null");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesEqualTo(String value) {
            addCriterion("config_singles =", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesNotEqualTo(String value) {
            addCriterion("config_singles <>", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesGreaterThan(String value) {
            addCriterion("config_singles >", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesGreaterThanOrEqualTo(String value) {
            addCriterion("config_singles >=", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesLessThan(String value) {
            addCriterion("config_singles <", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesLessThanOrEqualTo(String value) {
            addCriterion("config_singles <=", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesLike(String value) {
            addCriterion("config_singles like", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesNotLike(String value) {
            addCriterion("config_singles not like", value, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesIn(List<String> values) {
            addCriterion("config_singles in", values, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesNotIn(List<String> values) {
            addCriterion("config_singles not in", values, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesBetween(String value1, String value2) {
            addCriterion("config_singles between", value1, value2, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigSinglesNotBetween(String value1, String value2) {
            addCriterion("config_singles not between", value1, value2, "configSingles");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsIsNull() {
            addCriterion("config_collections is null");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsIsNotNull() {
            addCriterion("config_collections is not null");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsEqualTo(String value) {
            addCriterion("config_collections =", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsNotEqualTo(String value) {
            addCriterion("config_collections <>", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsGreaterThan(String value) {
            addCriterion("config_collections >", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsGreaterThanOrEqualTo(String value) {
            addCriterion("config_collections >=", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsLessThan(String value) {
            addCriterion("config_collections <", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsLessThanOrEqualTo(String value) {
            addCriterion("config_collections <=", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsLike(String value) {
            addCriterion("config_collections like", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsNotLike(String value) {
            addCriterion("config_collections not like", value, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsIn(List<String> values) {
            addCriterion("config_collections in", values, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsNotIn(List<String> values) {
            addCriterion("config_collections not in", values, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsBetween(String value1, String value2) {
            addCriterion("config_collections between", value1, value2, "configCollections");
            return (Criteria) this;
        }

        public Criteria andConfigCollectionsNotBetween(String value1, String value2) {
            addCriterion("config_collections not between", value1, value2, "configCollections");
            return (Criteria) this;
        }

        public Criteria andExtraArgsIsNull() {
            addCriterion("extra_args is null");
            return (Criteria) this;
        }

        public Criteria andExtraArgsIsNotNull() {
            addCriterion("extra_args is not null");
            return (Criteria) this;
        }

        public Criteria andExtraArgsEqualTo(String value) {
            addCriterion("extra_args =", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsNotEqualTo(String value) {
            addCriterion("extra_args <>", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsGreaterThan(String value) {
            addCriterion("extra_args >", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsGreaterThanOrEqualTo(String value) {
            addCriterion("extra_args >=", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsLessThan(String value) {
            addCriterion("extra_args <", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsLessThanOrEqualTo(String value) {
            addCriterion("extra_args <=", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsLike(String value) {
            addCriterion("extra_args like", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsNotLike(String value) {
            addCriterion("extra_args not like", value, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsIn(List<String> values) {
            addCriterion("extra_args in", values, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsNotIn(List<String> values) {
            addCriterion("extra_args not in", values, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsBetween(String value1, String value2) {
            addCriterion("extra_args between", value1, value2, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andExtraArgsNotBetween(String value1, String value2) {
            addCriterion("extra_args not between", value1, value2, "extraArgs");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Date value) {
            addCriterion("created_time =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Date value) {
            addCriterion("created_time <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Date value) {
            addCriterion("created_time >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("created_time >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Date value) {
            addCriterion("created_time <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Date value) {
            addCriterion("created_time <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Date> values) {
            addCriterion("created_time in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Date> values) {
            addCriterion("created_time not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Date value1, Date value2) {
            addCriterion("created_time between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Date value1, Date value2) {
            addCriterion("created_time not between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedByIsNull() {
            addCriterion("created_by is null");
            return (Criteria) this;
        }

        public Criteria andCreatedByIsNotNull() {
            addCriterion("created_by is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedByEqualTo(String value) {
            addCriterion("created_by =", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByNotEqualTo(String value) {
            addCriterion("created_by <>", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByGreaterThan(String value) {
            addCriterion("created_by >", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByGreaterThanOrEqualTo(String value) {
            addCriterion("created_by >=", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByLessThan(String value) {
            addCriterion("created_by <", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByLessThanOrEqualTo(String value) {
            addCriterion("created_by <=", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByLike(String value) {
            addCriterion("created_by like", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByNotLike(String value) {
            addCriterion("created_by not like", value, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByIn(List<String> values) {
            addCriterion("created_by in", values, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByNotIn(List<String> values) {
            addCriterion("created_by not in", values, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByBetween(String value1, String value2) {
            addCriterion("created_by between", value1, value2, "createdBy");
            return (Criteria) this;
        }

        public Criteria andCreatedByNotBetween(String value1, String value2) {
            addCriterion("created_by not between", value1, value2, "createdBy");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeIsNull() {
            addCriterion("modified_time is null");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeIsNotNull() {
            addCriterion("modified_time is not null");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeEqualTo(Date value) {
            addCriterion("modified_time =", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeNotEqualTo(Date value) {
            addCriterion("modified_time <>", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeGreaterThan(Date value) {
            addCriterion("modified_time >", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("modified_time >=", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeLessThan(Date value) {
            addCriterion("modified_time <", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeLessThanOrEqualTo(Date value) {
            addCriterion("modified_time <=", value, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeIn(List<Date> values) {
            addCriterion("modified_time in", values, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeNotIn(List<Date> values) {
            addCriterion("modified_time not in", values, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeBetween(Date value1, Date value2) {
            addCriterion("modified_time between", value1, value2, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedTimeNotBetween(Date value1, Date value2) {
            addCriterion("modified_time not between", value1, value2, "modifiedTime");
            return (Criteria) this;
        }

        public Criteria andModifiedByIsNull() {
            addCriterion("modified_by is null");
            return (Criteria) this;
        }

        public Criteria andModifiedByIsNotNull() {
            addCriterion("modified_by is not null");
            return (Criteria) this;
        }

        public Criteria andModifiedByEqualTo(String value) {
            addCriterion("modified_by =", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByNotEqualTo(String value) {
            addCriterion("modified_by <>", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByGreaterThan(String value) {
            addCriterion("modified_by >", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByGreaterThanOrEqualTo(String value) {
            addCriterion("modified_by >=", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByLessThan(String value) {
            addCriterion("modified_by <", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByLessThanOrEqualTo(String value) {
            addCriterion("modified_by <=", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByLike(String value) {
            addCriterion("modified_by like", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByNotLike(String value) {
            addCriterion("modified_by not like", value, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByIn(List<String> values) {
            addCriterion("modified_by in", values, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByNotIn(List<String> values) {
            addCriterion("modified_by not in", values, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByBetween(String value1, String value2) {
            addCriterion("modified_by between", value1, value2, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andModifiedByNotBetween(String value1, String value2) {
            addCriterion("modified_by not between", value1, value2, "modifiedBy");
            return (Criteria) this;
        }

        public Criteria andIsPerfIsNull() {
            addCriterion("is_perf is null");
            return (Criteria) this;
        }

        public Criteria andIsPerfIsNotNull() {
            addCriterion("is_perf is not null");
            return (Criteria) this;
        }

        public Criteria andIsPerfEqualTo(Boolean value) {
            addCriterion("is_perf =", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfNotEqualTo(Boolean value) {
            addCriterion("is_perf <>", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfGreaterThan(Boolean value) {
            addCriterion("is_perf >", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_perf >=", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfLessThan(Boolean value) {
            addCriterion("is_perf <", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfLessThanOrEqualTo(Boolean value) {
            addCriterion("is_perf <=", value, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfIn(List<Boolean> values) {
            addCriterion("is_perf in", values, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfNotIn(List<Boolean> values) {
            addCriterion("is_perf not in", values, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfBetween(Boolean value1, Boolean value2) {
            addCriterion("is_perf between", value1, value2, "isPerf");
            return (Criteria) this;
        }

        public Criteria andIsPerfNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_perf not between", value1, value2, "isPerf");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrIsNull() {
            addCriterion("dashboard_addr is null");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrIsNotNull() {
            addCriterion("dashboard_addr is not null");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrEqualTo(String value) {
            addCriterion("dashboard_addr =", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrNotEqualTo(String value) {
            addCriterion("dashboard_addr <>", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrGreaterThan(String value) {
            addCriterion("dashboard_addr >", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrGreaterThanOrEqualTo(String value) {
            addCriterion("dashboard_addr >=", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrLessThan(String value) {
            addCriterion("dashboard_addr <", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrLessThanOrEqualTo(String value) {
            addCriterion("dashboard_addr <=", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrLike(String value) {
            addCriterion("dashboard_addr like", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrNotLike(String value) {
            addCriterion("dashboard_addr not like", value, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrIn(List<String> values) {
            addCriterion("dashboard_addr in", values, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrNotIn(List<String> values) {
            addCriterion("dashboard_addr not in", values, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrBetween(String value1, String value2) {
            addCriterion("dashboard_addr between", value1, value2, "dashboardAddr");
            return (Criteria) this;
        }

        public Criteria andDashboardAddrNotBetween(String value1, String value2) {
            addCriterion("dashboard_addr not between", value1, value2, "dashboardAddr");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}