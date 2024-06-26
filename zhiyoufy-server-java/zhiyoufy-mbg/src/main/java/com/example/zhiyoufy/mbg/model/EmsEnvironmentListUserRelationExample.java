package com.example.zhiyoufy.mbg.model;

import java.util.ArrayList;
import java.util.List;

public class EmsEnvironmentListUserRelationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EmsEnvironmentListUserRelationExample() {
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

        public Criteria andEnvironmentListIdIsNull() {
            addCriterion("environment_list_id is null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdIsNotNull() {
            addCriterion("environment_list_id is not null");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdEqualTo(Long value) {
            addCriterion("environment_list_id =", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdNotEqualTo(Long value) {
            addCriterion("environment_list_id <>", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdGreaterThan(Long value) {
            addCriterion("environment_list_id >", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdGreaterThanOrEqualTo(Long value) {
            addCriterion("environment_list_id >=", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdLessThan(Long value) {
            addCriterion("environment_list_id <", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdLessThanOrEqualTo(Long value) {
            addCriterion("environment_list_id <=", value, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdIn(List<Long> values) {
            addCriterion("environment_list_id in", values, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdNotIn(List<Long> values) {
            addCriterion("environment_list_id not in", values, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdBetween(Long value1, Long value2) {
            addCriterion("environment_list_id between", value1, value2, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andEnvironmentListIdNotBetween(Long value1, Long value2) {
            addCriterion("environment_list_id not between", value1, value2, "environmentListId");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(Long value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(Long value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(Long value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(Long value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(Long value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(Long value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<Long> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<Long> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(Long value1, Long value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(Long value1, Long value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andIsOwnerIsNull() {
            addCriterion("is_owner is null");
            return (Criteria) this;
        }

        public Criteria andIsOwnerIsNotNull() {
            addCriterion("is_owner is not null");
            return (Criteria) this;
        }

        public Criteria andIsOwnerEqualTo(Boolean value) {
            addCriterion("is_owner =", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerNotEqualTo(Boolean value) {
            addCriterion("is_owner <>", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerGreaterThan(Boolean value) {
            addCriterion("is_owner >", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_owner >=", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerLessThan(Boolean value) {
            addCriterion("is_owner <", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerLessThanOrEqualTo(Boolean value) {
            addCriterion("is_owner <=", value, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerIn(List<Boolean> values) {
            addCriterion("is_owner in", values, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerNotIn(List<Boolean> values) {
            addCriterion("is_owner not in", values, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerBetween(Boolean value1, Boolean value2) {
            addCriterion("is_owner between", value1, value2, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsOwnerNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_owner not between", value1, value2, "isOwner");
            return (Criteria) this;
        }

        public Criteria andIsEditorIsNull() {
            addCriterion("is_editor is null");
            return (Criteria) this;
        }

        public Criteria andIsEditorIsNotNull() {
            addCriterion("is_editor is not null");
            return (Criteria) this;
        }

        public Criteria andIsEditorEqualTo(Boolean value) {
            addCriterion("is_editor =", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorNotEqualTo(Boolean value) {
            addCriterion("is_editor <>", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorGreaterThan(Boolean value) {
            addCriterion("is_editor >", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorGreaterThanOrEqualTo(Boolean value) {
            addCriterion("is_editor >=", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorLessThan(Boolean value) {
            addCriterion("is_editor <", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorLessThanOrEqualTo(Boolean value) {
            addCriterion("is_editor <=", value, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorIn(List<Boolean> values) {
            addCriterion("is_editor in", values, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorNotIn(List<Boolean> values) {
            addCriterion("is_editor not in", values, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorBetween(Boolean value1, Boolean value2) {
            addCriterion("is_editor between", value1, value2, "isEditor");
            return (Criteria) this;
        }

        public Criteria andIsEditorNotBetween(Boolean value1, Boolean value2) {
            addCriterion("is_editor not between", value1, value2, "isEditor");
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