package com.example.zhiyoufy.mbg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JmsJobScheduleExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public JmsJobScheduleExample() {
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

        public Criteria andProjectNameIsNull() {
            addCriterion("project_name is null");
            return (Criteria) this;
        }

        public Criteria andProjectNameIsNotNull() {
            addCriterion("project_name is not null");
            return (Criteria) this;
        }

        public Criteria andProjectNameEqualTo(String value) {
            addCriterion("project_name =", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotEqualTo(String value) {
            addCriterion("project_name <>", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameGreaterThan(String value) {
            addCriterion("project_name >", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameGreaterThanOrEqualTo(String value) {
            addCriterion("project_name >=", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLessThan(String value) {
            addCriterion("project_name <", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLessThanOrEqualTo(String value) {
            addCriterion("project_name <=", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameLike(String value) {
            addCriterion("project_name like", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotLike(String value) {
            addCriterion("project_name not like", value, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameIn(List<String> values) {
            addCriterion("project_name in", values, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotIn(List<String> values) {
            addCriterion("project_name not in", values, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameBetween(String value1, String value2) {
            addCriterion("project_name between", value1, value2, "projectName");
            return (Criteria) this;
        }

        public Criteria andProjectNameNotBetween(String value1, String value2) {
            addCriterion("project_name not between", value1, value2, "projectName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdIsNull() {
            addCriterion("worker_app_id is null");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdIsNotNull() {
            addCriterion("worker_app_id is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdEqualTo(Long value) {
            addCriterion("worker_app_id =", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdNotEqualTo(Long value) {
            addCriterion("worker_app_id <>", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdGreaterThan(Long value) {
            addCriterion("worker_app_id >", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdGreaterThanOrEqualTo(Long value) {
            addCriterion("worker_app_id >=", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdLessThan(Long value) {
            addCriterion("worker_app_id <", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdLessThanOrEqualTo(Long value) {
            addCriterion("worker_app_id <=", value, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdIn(List<Long> values) {
            addCriterion("worker_app_id in", values, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdNotIn(List<Long> values) {
            addCriterion("worker_app_id not in", values, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdBetween(Long value1, Long value2) {
            addCriterion("worker_app_id between", value1, value2, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppIdNotBetween(Long value1, Long value2) {
            addCriterion("worker_app_id not between", value1, value2, "workerAppId");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameIsNull() {
            addCriterion("worker_app_name is null");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameIsNotNull() {
            addCriterion("worker_app_name is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameEqualTo(String value) {
            addCriterion("worker_app_name =", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameNotEqualTo(String value) {
            addCriterion("worker_app_name <>", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameGreaterThan(String value) {
            addCriterion("worker_app_name >", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("worker_app_name >=", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameLessThan(String value) {
            addCriterion("worker_app_name <", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameLessThanOrEqualTo(String value) {
            addCriterion("worker_app_name <=", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameLike(String value) {
            addCriterion("worker_app_name like", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameNotLike(String value) {
            addCriterion("worker_app_name not like", value, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameIn(List<String> values) {
            addCriterion("worker_app_name in", values, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameNotIn(List<String> values) {
            addCriterion("worker_app_name not in", values, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameBetween(String value1, String value2) {
            addCriterion("worker_app_name between", value1, value2, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerAppNameNotBetween(String value1, String value2) {
            addCriterion("worker_app_name not between", value1, value2, "workerAppName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIsNull() {
            addCriterion("worker_group_id is null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIsNotNull() {
            addCriterion("worker_group_id is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdEqualTo(Long value) {
            addCriterion("worker_group_id =", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotEqualTo(Long value) {
            addCriterion("worker_group_id <>", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdGreaterThan(Long value) {
            addCriterion("worker_group_id >", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdGreaterThanOrEqualTo(Long value) {
            addCriterion("worker_group_id >=", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdLessThan(Long value) {
            addCriterion("worker_group_id <", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdLessThanOrEqualTo(Long value) {
            addCriterion("worker_group_id <=", value, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdIn(List<Long> values) {
            addCriterion("worker_group_id in", values, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotIn(List<Long> values) {
            addCriterion("worker_group_id not in", values, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdBetween(Long value1, Long value2) {
            addCriterion("worker_group_id between", value1, value2, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupIdNotBetween(Long value1, Long value2) {
            addCriterion("worker_group_id not between", value1, value2, "workerGroupId");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameIsNull() {
            addCriterion("worker_group_name is null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameIsNotNull() {
            addCriterion("worker_group_name is not null");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameEqualTo(String value) {
            addCriterion("worker_group_name =", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameNotEqualTo(String value) {
            addCriterion("worker_group_name <>", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameGreaterThan(String value) {
            addCriterion("worker_group_name >", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameGreaterThanOrEqualTo(String value) {
            addCriterion("worker_group_name >=", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameLessThan(String value) {
            addCriterion("worker_group_name <", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameLessThanOrEqualTo(String value) {
            addCriterion("worker_group_name <=", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameLike(String value) {
            addCriterion("worker_group_name like", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameNotLike(String value) {
            addCriterion("worker_group_name not like", value, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameIn(List<String> values) {
            addCriterion("worker_group_name in", values, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameNotIn(List<String> values) {
            addCriterion("worker_group_name not in", values, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameBetween(String value1, String value2) {
            addCriterion("worker_group_name between", value1, value2, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andWorkerGroupNameNotBetween(String value1, String value2) {
            addCriterion("worker_group_name not between", value1, value2, "workerGroupName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdIsNull() {
            addCriterion("environment_id is null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdIsNotNull() {
            addCriterion("environment_id is not null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdEqualTo(Long value) {
            addCriterion("environment_id =", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdNotEqualTo(Long value) {
            addCriterion("environment_id <>", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdGreaterThan(Long value) {
            addCriterion("environment_id >", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("environment_id >=", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdLessThan(Long value) {
            addCriterion("environment_id <", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdLessThanOrEqualTo(Long value) {
            addCriterion("environment_id <=", value, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdIn(List<Long> values) {
            addCriterion("environment_id in", values, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdNotIn(List<Long> values) {
            addCriterion("environment_id not in", values, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdBetween(Long value1, Long value2) {
            addCriterion("environment_id between", value1, value2, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentIdNotBetween(Long value1, Long value2) {
            addCriterion("environment_id not between", value1, value2, "environmentId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameIsNull() {
            addCriterion("environment_name is null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameIsNotNull() {
            addCriterion("environment_name is not null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameEqualTo(String value) {
            addCriterion("environment_name =", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameNotEqualTo(String value) {
            addCriterion("environment_name <>", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameGreaterThan(String value) {
            addCriterion("environment_name >", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameGreaterThanOrEqualTo(String value) {
            addCriterion("environment_name >=", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameLessThan(String value) {
            addCriterion("environment_name <", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameLessThanOrEqualTo(String value) {
            addCriterion("environment_name <=", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameLike(String value) {
            addCriterion("environment_name like", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameNotLike(String value) {
            addCriterion("environment_name not like", value, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameIn(List<String> values) {
            addCriterion("environment_name in", values, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameNotIn(List<String> values) {
            addCriterion("environment_name not in", values, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameBetween(String value1, String value2) {
            addCriterion("environment_name between", value1, value2, "environmentName");
            return (Criteria) this;
        }

        public Criteria andEnvironmentNameNotBetween(String value1, String value2) {
            addCriterion("environment_name not between", value1, value2, "environmentName");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNull() {
            addCriterion("template_id is null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIsNotNull() {
            addCriterion("template_id is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateIdEqualTo(Long value) {
            addCriterion("template_id =", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotEqualTo(Long value) {
            addCriterion("template_id <>", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThan(Long value) {
            addCriterion("template_id >", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdGreaterThanOrEqualTo(Long value) {
            addCriterion("template_id >=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThan(Long value) {
            addCriterion("template_id <", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdLessThanOrEqualTo(Long value) {
            addCriterion("template_id <=", value, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdIn(List<Long> values) {
            addCriterion("template_id in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotIn(List<Long> values) {
            addCriterion("template_id not in", values, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdBetween(Long value1, Long value2) {
            addCriterion("template_id between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateIdNotBetween(Long value1, Long value2) {
            addCriterion("template_id not between", value1, value2, "templateId");
            return (Criteria) this;
        }

        public Criteria andTemplateNameIsNull() {
            addCriterion("template_name is null");
            return (Criteria) this;
        }

        public Criteria andTemplateNameIsNotNull() {
            addCriterion("template_name is not null");
            return (Criteria) this;
        }

        public Criteria andTemplateNameEqualTo(String value) {
            addCriterion("template_name =", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameNotEqualTo(String value) {
            addCriterion("template_name <>", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameGreaterThan(String value) {
            addCriterion("template_name >", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameGreaterThanOrEqualTo(String value) {
            addCriterion("template_name >=", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameLessThan(String value) {
            addCriterion("template_name <", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameLessThanOrEqualTo(String value) {
            addCriterion("template_name <=", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameLike(String value) {
            addCriterion("template_name like", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameNotLike(String value) {
            addCriterion("template_name not like", value, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameIn(List<String> values) {
            addCriterion("template_name in", values, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameNotIn(List<String> values) {
            addCriterion("template_name not in", values, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameBetween(String value1, String value2) {
            addCriterion("template_name between", value1, value2, "templateName");
            return (Criteria) this;
        }

        public Criteria andTemplateNameNotBetween(String value1, String value2) {
            addCriterion("template_name not between", value1, value2, "templateName");
            return (Criteria) this;
        }

        public Criteria andRunTagIsNull() {
            addCriterion("run_tag is null");
            return (Criteria) this;
        }

        public Criteria andRunTagIsNotNull() {
            addCriterion("run_tag is not null");
            return (Criteria) this;
        }

        public Criteria andRunTagEqualTo(String value) {
            addCriterion("run_tag =", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagNotEqualTo(String value) {
            addCriterion("run_tag <>", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagGreaterThan(String value) {
            addCriterion("run_tag >", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagGreaterThanOrEqualTo(String value) {
            addCriterion("run_tag >=", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagLessThan(String value) {
            addCriterion("run_tag <", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagLessThanOrEqualTo(String value) {
            addCriterion("run_tag <=", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagLike(String value) {
            addCriterion("run_tag like", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagNotLike(String value) {
            addCriterion("run_tag not like", value, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagIn(List<String> values) {
            addCriterion("run_tag in", values, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagNotIn(List<String> values) {
            addCriterion("run_tag not in", values, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagBetween(String value1, String value2) {
            addCriterion("run_tag between", value1, value2, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunTagNotBetween(String value1, String value2) {
            addCriterion("run_tag not between", value1, value2, "runTag");
            return (Criteria) this;
        }

        public Criteria andRunNumIsNull() {
            addCriterion("run_num is null");
            return (Criteria) this;
        }

        public Criteria andRunNumIsNotNull() {
            addCriterion("run_num is not null");
            return (Criteria) this;
        }

        public Criteria andRunNumEqualTo(Integer value) {
            addCriterion("run_num =", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumNotEqualTo(Integer value) {
            addCriterion("run_num <>", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumGreaterThan(Integer value) {
            addCriterion("run_num >", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("run_num >=", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumLessThan(Integer value) {
            addCriterion("run_num <", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumLessThanOrEqualTo(Integer value) {
            addCriterion("run_num <=", value, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumIn(List<Integer> values) {
            addCriterion("run_num in", values, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumNotIn(List<Integer> values) {
            addCriterion("run_num not in", values, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumBetween(Integer value1, Integer value2) {
            addCriterion("run_num between", value1, value2, "runNum");
            return (Criteria) this;
        }

        public Criteria andRunNumNotBetween(Integer value1, Integer value2) {
            addCriterion("run_num not between", value1, value2, "runNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumIsNull() {
            addCriterion("parallel_num is null");
            return (Criteria) this;
        }

        public Criteria andParallelNumIsNotNull() {
            addCriterion("parallel_num is not null");
            return (Criteria) this;
        }

        public Criteria andParallelNumEqualTo(Integer value) {
            addCriterion("parallel_num =", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumNotEqualTo(Integer value) {
            addCriterion("parallel_num <>", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumGreaterThan(Integer value) {
            addCriterion("parallel_num >", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumGreaterThanOrEqualTo(Integer value) {
            addCriterion("parallel_num >=", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumLessThan(Integer value) {
            addCriterion("parallel_num <", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumLessThanOrEqualTo(Integer value) {
            addCriterion("parallel_num <=", value, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumIn(List<Integer> values) {
            addCriterion("parallel_num in", values, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumNotIn(List<Integer> values) {
            addCriterion("parallel_num not in", values, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumBetween(Integer value1, Integer value2) {
            addCriterion("parallel_num between", value1, value2, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andParallelNumNotBetween(Integer value1, Integer value2) {
            addCriterion("parallel_num not between", value1, value2, "parallelNum");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsIsNull() {
            addCriterion("include_tags is null");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsIsNotNull() {
            addCriterion("include_tags is not null");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsEqualTo(String value) {
            addCriterion("include_tags =", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsNotEqualTo(String value) {
            addCriterion("include_tags <>", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsGreaterThan(String value) {
            addCriterion("include_tags >", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsGreaterThanOrEqualTo(String value) {
            addCriterion("include_tags >=", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsLessThan(String value) {
            addCriterion("include_tags <", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsLessThanOrEqualTo(String value) {
            addCriterion("include_tags <=", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsLike(String value) {
            addCriterion("include_tags like", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsNotLike(String value) {
            addCriterion("include_tags not like", value, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsIn(List<String> values) {
            addCriterion("include_tags in", values, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsNotIn(List<String> values) {
            addCriterion("include_tags not in", values, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsBetween(String value1, String value2) {
            addCriterion("include_tags between", value1, value2, "includeTags");
            return (Criteria) this;
        }

        public Criteria andIncludeTagsNotBetween(String value1, String value2) {
            addCriterion("include_tags not between", value1, value2, "includeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsIsNull() {
            addCriterion("exclude_tags is null");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsIsNotNull() {
            addCriterion("exclude_tags is not null");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsEqualTo(String value) {
            addCriterion("exclude_tags =", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsNotEqualTo(String value) {
            addCriterion("exclude_tags <>", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsGreaterThan(String value) {
            addCriterion("exclude_tags >", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsGreaterThanOrEqualTo(String value) {
            addCriterion("exclude_tags >=", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsLessThan(String value) {
            addCriterion("exclude_tags <", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsLessThanOrEqualTo(String value) {
            addCriterion("exclude_tags <=", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsLike(String value) {
            addCriterion("exclude_tags like", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsNotLike(String value) {
            addCriterion("exclude_tags not like", value, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsIn(List<String> values) {
            addCriterion("exclude_tags in", values, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsNotIn(List<String> values) {
            addCriterion("exclude_tags not in", values, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsBetween(String value1, String value2) {
            addCriterion("exclude_tags between", value1, value2, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andExcludeTagsNotBetween(String value1, String value2) {
            addCriterion("exclude_tags not between", value1, value2, "excludeTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsIsNull() {
            addCriterion("add_tags is null");
            return (Criteria) this;
        }

        public Criteria andAddTagsIsNotNull() {
            addCriterion("add_tags is not null");
            return (Criteria) this;
        }

        public Criteria andAddTagsEqualTo(String value) {
            addCriterion("add_tags =", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsNotEqualTo(String value) {
            addCriterion("add_tags <>", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsGreaterThan(String value) {
            addCriterion("add_tags >", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsGreaterThanOrEqualTo(String value) {
            addCriterion("add_tags >=", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsLessThan(String value) {
            addCriterion("add_tags <", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsLessThanOrEqualTo(String value) {
            addCriterion("add_tags <=", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsLike(String value) {
            addCriterion("add_tags like", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsNotLike(String value) {
            addCriterion("add_tags not like", value, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsIn(List<String> values) {
            addCriterion("add_tags in", values, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsNotIn(List<String> values) {
            addCriterion("add_tags not in", values, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsBetween(String value1, String value2) {
            addCriterion("add_tags between", value1, value2, "addTags");
            return (Criteria) this;
        }

        public Criteria andAddTagsNotBetween(String value1, String value2) {
            addCriterion("add_tags not between", value1, value2, "addTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsIsNull() {
            addCriterion("remove_tags is null");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsIsNotNull() {
            addCriterion("remove_tags is not null");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsEqualTo(String value) {
            addCriterion("remove_tags =", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsNotEqualTo(String value) {
            addCriterion("remove_tags <>", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsGreaterThan(String value) {
            addCriterion("remove_tags >", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsGreaterThanOrEqualTo(String value) {
            addCriterion("remove_tags >=", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsLessThan(String value) {
            addCriterion("remove_tags <", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsLessThanOrEqualTo(String value) {
            addCriterion("remove_tags <=", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsLike(String value) {
            addCriterion("remove_tags like", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsNotLike(String value) {
            addCriterion("remove_tags not like", value, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsIn(List<String> values) {
            addCriterion("remove_tags in", values, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsNotIn(List<String> values) {
            addCriterion("remove_tags not in", values, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsBetween(String value1, String value2) {
            addCriterion("remove_tags between", value1, value2, "removeTags");
            return (Criteria) this;
        }

        public Criteria andRemoveTagsNotBetween(String value1, String value2) {
            addCriterion("remove_tags not between", value1, value2, "removeTags");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigIsNull() {
            addCriterion("crontab_config is null");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigIsNotNull() {
            addCriterion("crontab_config is not null");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigEqualTo(String value) {
            addCriterion("crontab_config =", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigNotEqualTo(String value) {
            addCriterion("crontab_config <>", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigGreaterThan(String value) {
            addCriterion("crontab_config >", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigGreaterThanOrEqualTo(String value) {
            addCriterion("crontab_config >=", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigLessThan(String value) {
            addCriterion("crontab_config <", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigLessThanOrEqualTo(String value) {
            addCriterion("crontab_config <=", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigLike(String value) {
            addCriterion("crontab_config like", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigNotLike(String value) {
            addCriterion("crontab_config not like", value, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigIn(List<String> values) {
            addCriterion("crontab_config in", values, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigNotIn(List<String> values) {
            addCriterion("crontab_config not in", values, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigBetween(String value1, String value2) {
            addCriterion("crontab_config between", value1, value2, "crontabConfig");
            return (Criteria) this;
        }

        public Criteria andCrontabConfigNotBetween(String value1, String value2) {
            addCriterion("crontab_config not between", value1, value2, "crontabConfig");
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