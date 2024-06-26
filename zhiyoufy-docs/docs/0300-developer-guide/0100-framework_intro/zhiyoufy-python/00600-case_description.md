---
title: Case描述文件
---

## 介绍

**datas列表**中包含的就是对应case的测试步骤  

我们可以看到每个测试步骤都有一个`type`字段，`TestDynamicFlowBase.run`就是根据这个type来判断该调用哪个handler来处理。

每个type的命名都要以将要处理它的handler的`type_prefix`作为前缀，比如type`zhiyoufy_base_login`表明要由`zhiyoufy_`对应的handler来处理 

## 示例

可以参考`zhiyoufy-python`项目中的
`data/templates/dynamic_flow_test/00100__config_single_crud.json.j2`

```json
{
  "params": {
    "zhiyoufy_addr": "{{ zhiyoufy.addr }}",
    "case_description": [
        "使用user_params_group_3用户对特定的config single做CRUD操作"
    ]
  },
  "datas": [
    {
      "type": "zhiyoufy_base_login",
      "username": "{{ zhiyoufy.user_params_group_3.username }}",
      "password": "{{ zhiyoufy.user_params_group_3.password }}"
    },


    {
        "type": "zhiyoufy_environment_get_single_by_name",
        "environment_var_path": "environment_var",
        "name": "{{ env_name }}",
        "must_exist": true,
        "step_description": "查找名字为name的environment"
    },


    {
      "type": "zhiyoufy_config_single_get_single_by_name",
      "environment_var_path": "environment_var",
      "config_single_var_path": "config_single_var",
      "name": "config_single_for_test_crud_{{ params_group_4_item_name }}",
      "step_description": "查找名字为name的config_single"
    },
    {
      "type": "zhiyoufy_config_single_del",
      "config_single_var_path": "config_single_var",
      "step_description": [
        "如果存在，删除指定config_single"
      ]
    },
    {
      "type": "zhiyoufy_config_single_create",
      "environment_var_path": "environment_var",
      "name": "config_single_for_test_crud_{{ params_group_4_item_name }}",
      "configValue": "config_single_for_test_crud_phase: created",
      "step_description": "创建"
    },
    {
      "type": "zhiyoufy_config_single_get_single_by_name",
      "environment_var_path": "environment_var",
      "config_single_var_path": "config_single_var",
      "name": "config_single_for_test_crud_{{ params_group_4_item_name }}",
      "must_exist": true,
      "step_description": "查找名字为name的config_single"
    },
    {
      "type": "zhiyoufy_config_single_update",
      "config_single_var_path": "config_single_var",
      "configValue": "config_single_for_test_crud_phase: updated",
      "step_description": "创建"
    },
    {
      "type": "zhiyoufy_config_single_get_single_by_name",
      "environment_var_path": "environment_var",
      "config_single_var_path": "config_single_var",
      "name": "config_single_for_test_crud_{{ params_group_4_item_name }}",
      "must_exist": true,
      "expected_rsp": {
        "configValue": "config_single_for_test_crud_phase: updated"
      },
      "step_description": "查找名字为name的config_single"
    },
    {
      "type": "zhiyoufy_config_single_del",
      "config_single_var_path": "config_single_var",
      "step_description": [
        "如果存在，删除指定config_single"
      ]
    },

    {
      "type": "general_sleep",
{% if config_single_crud_time_seconds is defined %}
      "time_seconds": {{ config_single_crud_time_seconds }}
{% else %}
      "time_seconds": 2
{% endif %}
    },


    {
      "type": "zhiyoufy_base_logout"
    }
  ]
}
```
