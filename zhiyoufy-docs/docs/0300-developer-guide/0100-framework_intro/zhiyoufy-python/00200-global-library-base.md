---
title: GlobalLibraryBase
---

这个是一个全局单例库，用于完成框架的初始化工作，然后持有一些全局共享的数据

## 配置数据

配置数据使用[HOCON][]格式，使用[pyhocon][]解析，然后保存在zhiyoufy_context中

```python
class GlobalLibraryBase:
    def __init__(self, config_file):

        init_app_base.init_app_base(config_file)        
```

```python
def init_app_base(config_path, init_app_default_configs=None, check_dir_names=None):

    config_inst = config.params_from_file(config_file, overrides)

    zhiyoufy_context.set_config_inst(config_inst)
```

### 缺省配置数据

为了方便使用，配置项应有缺省值，用户应只需指定与缺省值不同的配置，或者为了明确而指定

```python
class GlobalLibraryBase:
    def __init__(self, config_file):

        self.init_default_config_base()
        self.init_default_config()

    def init_default_config_base(self):

        if "rm_old_days" not in self.config_inst:
            self.config_inst.rm_old_days = 30                    
``` 

## 日志管理

初始化logging相关，比如配置输出目的地, console, file或者都，配置log保存时间等

```python
class GlobalLibraryBase:
    def __init__(self, config_file):

        init_app_base.init_app_base(config_file)

def init_app_base(config_path, init_app_default_configs=None, check_dir_names=None):

    setup_logging()
    setup_normal_logger()
    setup_robot_logger()     

def setup_logging():

    if "file_handler" in config_inst.log_handlers:
        log_config["handlers"]["file_handler"]["filename"] = os.path.join(log_dir, "app.log")
        log_config["handlers"]["file_handler"]["backupCount"] = config_inst.log_backupCount
    else:
        del log_config["handlers"]["file_handler"]
        log_config["root"]["handlers"].remove("file_handler")
        file_handler_on = False                         
```

## 运行时数据

我们的case描述模板中定义了一个个的测试步骤，在执行过程中，后面的测试步骤有可能需要使用到前面测试步骤得到的结果，这些结果数据我们称之为运行时数据Run-Time Data。 `GlobalLibraryBase`帮我们实现了这些Run-Time Data的保存和获取，可以作为Global Variable在所有类和函数中共享这些数据。  

### step_vars_map

`GlobalLibraryBase`中定义了*step_vars_map*用来保存Run-Time Data。使用时没有特别的要求，map的key值和value值都由使用者自己决定。

### 如何使用step_vars_map

比如， 我在某个测试步骤中拿到了**environment_var**值，而在后面的步骤中又要使用这个**environment_var**，那么我可以这样实现：

1. 配置**environment_var**
  
```json
    {
        "type": "zhiyoufy_environment_get_single_by_name",
        "environment_var_path": "environment_var",
        "name": "{{ env_name }}",
        "must_exist": true,
        "step_description": "查找名字为name的environment"
    },
```

```python
class ZhiyoufyClientEnvironment(ZhiyoufyClientBridge):

    def environment_get_single_by_name(self, params, trace_on=True):

            environment_var_path = params.get("environment_var_path", None)

            if environment_var_path:
                self.set_step_var(environment_var_path, target_environment)                    
```

2. 使用**environment_var**

```json
    {
      "type": "zhiyoufy_config_single_get_single_by_name",
      "environment_var_path": "environment_var",
      "config_single_var_path": "config_single_var",
      "name": "config_single_for_test_crud_{{ params_group_4_item_name }}",
      "step_description": "查找名字为name的config_single"
    },
```

```python
class ZhiyoufyClientConfigSingle(ZhiyoufyClientBridge):

    def config_single_get_single_by_name(self, params, trace_on=True):

            environment_var_path = params["environment_var_path"]

            config_single_list = self.config_single_get_list({
                "environment_var_path": environment_var_path,
                "exactMatch": True,
                "keyword": name,
            })

    def config_single_get_list(self, params, trace_on=True):

            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)                                   
```

[HOCON]: https://github.com/lightbend/config/blob/main/HOCON.md
[RobotFramework]: https://robotframework.org
[pyhocon]: https://github.com/chimpler/pyhocon
[jinja]: https://jinja.palletsprojects.com/en/2.10.x/
