---
title: 创建新case
---

下面以`zhiyoufy-python`中的`data/templates/dynamic_flow_test/00100__config_single_crud.json.j2`为例

## 构建case描述文件`*.json.j2`

里面的datas是一个列表，每项代表一个操作步骤，通常是顺序执行，类似编程语言，
框架里也封装了**loop，if**等步骤块

```json title=data/templates/dynamic_flow_test/00100__config_single_crud.json.j2
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
```    

## 调试case描述文件`*.json.j2`

调试的时候直接使用python，这样可以设置断点

```python
def debug_zhiyoufy_test(cases_list):
    GlobalLibraryBase.g_global_library_base = None
    GlobalLibrary.g_global_library = None
    GlobalLibrary.GlobalLibrary("config/zhiyoufy_test/robot.conf")

    for test_config in cases_list:
        test_dynamic_flow_inst = TestDynamicFlow()

        if isinstance(test_config, str):
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config)
        else:
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config[0], test_config[1])

if __name__ == '__main__':

        cases_list = [
            "dynamic_flow_test/00100__config_single_crud.json.j2",
        ]

    for trial in range(1):
        debug_zhiyoufy_test(cases_list)            
```

## 创建robot的suite文件

- `global_resource.robot`这个资源文件负责初始化`GlobalLibrary`
- `Library           zhiyoufy.app.TestDynamicFlow`这个负责初始化`TestDynamicFlow`
- `TestDynamicFlow.run`这个是调用对应函数运行

```text title=cases/zhiyoufy_test/dynamic_flow_test/01000__config_single_crud.robot
*** Settings ***
Documentation     A test suite for Dynamic Flow
...
...               Config Single CRUD
Resource          ../../global_resource.robot
Library           zhiyoufy.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags        nostat-00100__config_single_crud

*** Test Cases ***
Test Config Single CRUD
    TestDynamicFlow.run  dynamic_flows_from_tpl/dynamic_flow_test/00100__config_single_crud.json.j2
```
