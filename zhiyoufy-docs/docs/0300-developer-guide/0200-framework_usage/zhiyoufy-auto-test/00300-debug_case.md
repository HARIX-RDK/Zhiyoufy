---
title: 调试case
---

## 配置目标环境

被测产品被部署到了多个环境，在调试时候需要指定针对的是哪个环境

在`config/harix_test`目录下每个环境有对应的子目录保存对应环境的配置，比如
ingress的地址等

修改`config/harix_test/private.conf`指定目标环境，比如

```text title=config/harix_test/private.conf
include "env_86/combo.conf"
```

## 配置config_collection_item

case经常需要用到config_collection配置，那么运行就需要一个具体config_collection_item
配置，比如`params_group_1`

private_params目录下通常包含用到的不同config_collection_item配置，比如

```text title=config/harix_test/private.conf
include "private_params/combo.conf"
```

下面的配置里指定了`params_group_1`, `params_group_3`等config_collection_item

```text title=config/harix_test/private_params/combo.conf
include "params_group_1.conf"
include "params_group_3.conf"
include "params_group_6.conf"
include "params_group_10.conf"
include "smartmap_group_20.conf"
```

下面是一个具体config_collection_item的配置

(可以选一个编号靠后的机器人，减少对应配置被zhiyoufy平台使用从而冲突概率)

```text title=config/harix_test/private_params/params_group_1.conf
# configCollectionName params_group_1
# configItemName params_group_1__0062
############# ROC Settings
params_group_1_service_code: "ginger"
params_group_1_tenant_code: "testjobs001"
params_group_1_account_code: "autotest0010062"
params_group_1_device_id: "858858580010062"
params_group_1_robot_type: "Cloud Ginger"
############ HARI Settings
params_group_1_portal_user: "autotest0010062@cloudminds.com"
```

### 获取示例config_collection_items

可以通过在zhiyoufy上触发一次测试运行，然后在运行结果的http地址下载回来所用的配置，
比如某一次运行的输出url是`http://xxxx:xx/zhiyoufy-worker/harix-auto-test-worker-1661741282-c96cc9fd9-zcgml/jobs/d74e3385/260cc073-index-1/`，
在这个url下面有一个子目录`config/harix_test`，可以下载里面的`private.conf`文件，这个文件
的前半部分是config_single配置，后面是config_collection_item配置

## 临时调试文件

`.gitignore`里有配`*temp.py`，这样具体使用时候可以拷贝一个基准文件
到一个有`temp.py`后缀的文件，然后大家减少互相影响

- 复制debug_case_test_harix.py到debug_case_test_harix_temp.py
- 按需配置debug_target，调试单个脚本时通常为debug_dev
- 如果前面选了debug_dev，则在debug_dev设置case列表地方按需设置

```python
    elif debug_target == "debug_dev":
        cases_list = [
            "roc_test/robot_mgmt/create_account_upto_activate.json.j2",
        ]
```