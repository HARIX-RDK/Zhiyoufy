
## 添加Robot Framework的case文件

比如添加`cases/harix_test/navigation_test/0020__two_robots_delivery_test_0002.robot`文件

```text
*** Settings ***
Documentation     A test suite for Navigation.
Resource          ../../global_resource.robot
Library           harixautotest.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags  nostat-two_robots_delivery_test_0002

*** Test Cases ***
two_robots_delivery_test_0002
    TestDynamicFlow.run  dynamic_flows_from_tpl/navigation_test/two_robots_delivery_test_0001.json.j2  {"multi_robots_delivery_test_config":"two_robots_delivery_test_0002","robots_group_001":"robots_group_002"}
```

## 添加job template, config到后端

`debug_case_zhiyoufy_test.py`

```python
def debug_harix_auto_test_template():

        f"{harix_auto_test_template_dir}/prepare_folder_navigation_test.json.j2",

def debug_harix_auto_test_env_single_config_prepartion():

        f"{harix_config_single_dir}/navigation_test.json.j2",

def debug_harix_auto_test_env_collection_config_prepartion():

        f"{harix_config_collection_dir}/navigation_robots_group.json.j2",            
```    

## 在本地起worker验证

`run_worker.py`

