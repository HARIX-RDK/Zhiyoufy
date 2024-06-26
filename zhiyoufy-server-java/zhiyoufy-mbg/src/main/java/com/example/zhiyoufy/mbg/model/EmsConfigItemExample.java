package com.example.zhiyoufy.mbg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmsConfigItemExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmsConfigItemExample() {
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

        public Criteria andCollectionIdIsNull() {
            addCriterion("collection_id is null");
            return (Criteria) this;
        }

        public Criteria andCollectionIdIsNotNull() {
            addCriterion("collection_id is not null");
            return (Criteria) this;
        }

        public Criteria andCollectionIdEqualTo(Long value) {
            addCriterion("collection_id =", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdNotEqualTo(Long value) {
            addCriterion("collection_id <>", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdGreaterThan(Long value) {
            addCriterion("collection_id >", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdGreaterThanOrEqualTo(Long value) {
            addCriterion("collection_id >=", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdLessThan(Long value) {
            addCriterion("collection_id <", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdLessThanOrEqualTo(Long value) {
            addCriterion("collection_id <=", value, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdIn(List<Long> values) {
            addCriterion("collection_id in", values, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdNotIn(List<Long> values) {
            addCriterion("collection_id not in", values, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdBetween(Long value1, Long value2) {
            addCriterion("collection_id between", value1, value2, "collectionId");
            return (Criteria) this;
        }

        public Criteria andCollectionIdNotBetween(Long value1, Long value2) {
            addCriterion("collection_id not between", value1, value2, "collectionId");
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

        public Criteria andTagsIsNull() {
            addCriterion("tags is null");
            return (Criteria) this;
        }

        public Criteria andTagsIsNotNull() {
            addCriterion("tags is not null");
            return (Criteria) this;
        }

        public Criteria andTagsEqualTo(String value) {
            addCriterion("tags =", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsNotEqualTo(String value) {
            addCriterion("tags <>", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsGreaterThan(String value) {
            addCriterion("tags >", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsGreaterThanOrEqualTo(String value) {
            addCriterion("tags >=", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsLessThan(String value) {
            addCriterion("tags <", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsLessThanOrEqualTo(String value) {
            addCriterion("tags <=", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsLike(String value) {
            addCriterion("tags like", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsNotLike(String value) {
            addCriterion("tags not like", value, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsIn(List<String> values) {
            addCriterion("tags in", values, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsNotIn(List<String> values) {
            addCriterion("tags not in", values, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsBetween(String value1, String value2) {
            addCriterion("tags between", value1, value2, "tags");
            return (Criteria) this;
        }

        public Criteria andTagsNotBetween(String value1, String value2) {
            addCriterion("tags not between", value1, value2, "tags");
            return (Criteria) this;
        }

        public Criteria andSortIsNull() {
            addCriterion("sort is null");
            return (Criteria) this;
        }

        public Criteria andSortIsNotNull() {
            addCriterion("sort is not null");
            return (Criteria) this;
        }

        public Criteria andSortEqualTo(Integer value) {
            addCriterion("sort =", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotEqualTo(Integer value) {
            addCriterion("sort <>", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThan(Integer value) {
            addCriterion("sort >", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortGreaterThanOrEqualTo(Integer value) {
            addCriterion("sort >=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThan(Integer value) {
            addCriterion("sort <", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortLessThanOrEqualTo(Integer value) {
            addCriterion("sort <=", value, "sort");
            return (Criteria) this;
        }

        public Criteria andSortIn(List<Integer> values) {
            addCriterion("sort in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotIn(List<Integer> values) {
            addCriterion("sort not in", values, "sort");
            return (Criteria) this;
        }

        public Criteria andSortBetween(Integer value1, Integer value2) {
            addCriterion("sort between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andSortNotBetween(Integer value1, Integer value2) {
            addCriterion("sort not between", value1, value2, "sort");
            return (Criteria) this;
        }

        public Criteria andDisabledIsNull() {
            addCriterion("disabled is null");
            return (Criteria) this;
        }

        public Criteria andDisabledIsNotNull() {
            addCriterion("disabled is not null");
            return (Criteria) this;
        }

        public Criteria andDisabledEqualTo(Boolean value) {
            addCriterion("disabled =", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledNotEqualTo(Boolean value) {
            addCriterion("disabled <>", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledGreaterThan(Boolean value) {
            addCriterion("disabled >", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledGreaterThanOrEqualTo(Boolean value) {
            addCriterion("disabled >=", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledLessThan(Boolean value) {
            addCriterion("disabled <", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledLessThanOrEqualTo(Boolean value) {
            addCriterion("disabled <=", value, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledIn(List<Boolean> values) {
            addCriterion("disabled in", values, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledNotIn(List<Boolean> values) {
            addCriterion("disabled not in", values, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledBetween(Boolean value1, Boolean value2) {
            addCriterion("disabled between", value1, value2, "disabled");
            return (Criteria) this;
        }

        public Criteria andDisabledNotBetween(Boolean value1, Boolean value2) {
            addCriterion("disabled not between", value1, value2, "disabled");
            return (Criteria) this;
        }

        public Criteria andInUseIsNull() {
            addCriterion("in_use is null");
            return (Criteria) this;
        }

        public Criteria andInUseIsNotNull() {
            addCriterion("in_use is not null");
            return (Criteria) this;
        }

        public Criteria andInUseEqualTo(Boolean value) {
            addCriterion("in_use =", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseNotEqualTo(Boolean value) {
            addCriterion("in_use <>", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseGreaterThan(Boolean value) {
            addCriterion("in_use >", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseGreaterThanOrEqualTo(Boolean value) {
            addCriterion("in_use >=", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseLessThan(Boolean value) {
            addCriterion("in_use <", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseLessThanOrEqualTo(Boolean value) {
            addCriterion("in_use <=", value, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseIn(List<Boolean> values) {
            addCriterion("in_use in", values, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseNotIn(List<Boolean> values) {
            addCriterion("in_use not in", values, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseBetween(Boolean value1, Boolean value2) {
            addCriterion("in_use between", value1, value2, "inUse");
            return (Criteria) this;
        }

        public Criteria andInUseNotBetween(Boolean value1, Boolean value2) {
            addCriterion("in_use not between", value1, value2, "inUse");
            return (Criteria) this;
        }

        public Criteria andUsageIdIsNull() {
            addCriterion("usage_id is null");
            return (Criteria) this;
        }

        public Criteria andUsageIdIsNotNull() {
            addCriterion("usage_id is not null");
            return (Criteria) this;
        }

        public Criteria andUsageIdEqualTo(String value) {
            addCriterion("usage_id =", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdNotEqualTo(String value) {
            addCriterion("usage_id <>", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdGreaterThan(String value) {
            addCriterion("usage_id >", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdGreaterThanOrEqualTo(String value) {
            addCriterion("usage_id >=", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdLessThan(String value) {
            addCriterion("usage_id <", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdLessThanOrEqualTo(String value) {
            addCriterion("usage_id <=", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdLike(String value) {
            addCriterion("usage_id like", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdNotLike(String value) {
            addCriterion("usage_id not like", value, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdIn(List<String> values) {
            addCriterion("usage_id in", values, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdNotIn(List<String> values) {
            addCriterion("usage_id not in", values, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdBetween(String value1, String value2) {
            addCriterion("usage_id between", value1, value2, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageIdNotBetween(String value1, String value2) {
            addCriterion("usage_id not between", value1, value2, "usageId");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtIsNull() {
            addCriterion("usage_timeout_at is null");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtIsNotNull() {
            addCriterion("usage_timeout_at is not null");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtEqualTo(Date value) {
            addCriterion("usage_timeout_at =", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtNotEqualTo(Date value) {
            addCriterion("usage_timeout_at <>", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtGreaterThan(Date value) {
            addCriterion("usage_timeout_at >", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtGreaterThanOrEqualTo(Date value) {
            addCriterion("usage_timeout_at >=", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtLessThan(Date value) {
            addCriterion("usage_timeout_at <", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtLessThanOrEqualTo(Date value) {
            addCriterion("usage_timeout_at <=", value, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtIn(List<Date> values) {
            addCriterion("usage_timeout_at in", values, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtNotIn(List<Date> values) {
            addCriterion("usage_timeout_at not in", values, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtBetween(Date value1, Date value2) {
            addCriterion("usage_timeout_at between", value1, value2, "usageTimeoutAt");
            return (Criteria) this;
        }

        public Criteria andUsageTimeoutAtNotBetween(Date value1, Date value2) {
            addCriterion("usage_timeout_at not between", value1, value2, "usageTimeoutAt");
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