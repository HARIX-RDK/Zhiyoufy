---
title: Handler
---

参与自动化测试的每个功能模块，我们都要为其定义一个handler类，指定一个type_prefix, 并实现下列函数
```
    self.type_prefix = "newmodule_client_"

    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.context = dynamic_flow_base_inst.context
        self.robot_logger = dynamic_flow_base_inst.robot_logger

        # "newmodule_client"是向TestDynamicFlowContext注册时，与parent client实例相对应的key值
        self.context.add_lib_client_factory("newmodule_client", self.build_newmodule_client)

    def cleanup(self, case_context):
        ##case执行结束时的清理工作
        pass

    def build_newmodule_client(self):
        ##所拥有的parent client的构造方法
        pass

    def process(self, data):
        # 根据data["type"]的值调用相应的client执行测试步骤
        pass
```

## handler封装

handler用于处理case描述文件中的具体步骤，一般一个被测产品封装一个handler，比如针对zhiyoufy就有
封装`DynamicFlowZhiyoufyClient`

### type_prefix

type_prefix用于指定这个handler处理的步骤type的前缀

```python
class DynamicFlowZhiyoufyClient:
    def __init__(self):

        self.type_prefix = "zhiyoufy_"
```

在handler内部中应该按照子模块进行进一步划分，比如

```python
    def process(self, data):
        if data['type'].startswith('zhiyoufy_base_'):
            self.process_type_of_zhiyoufy_base(data)
        elif data['type'].startswith('zhiyoufy_config_collection_'):
            self.process_type_of_zhiyoufy_config_collection(data)
        elif data['type'].startswith('zhiyoufy_config_item_'):
            self.process_type_of_zhiyoufy_config_item(data)
        elif data['type'].startswith('zhiyoufy_config_single_'):
            self.process_type_of_zhiyoufy_config_single(data)
```     

## handler加载

handler需要挂载到TestDynamicFlowBase中，为了加载它需要固定几个函数，比如`setup`, `cleanup`

`setup`通常用于添加相关client到context中，比如

```python
    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.robot_logger = dynamic_flow_base_inst.robot_logger
        self.context = dynamic_flow_base_inst.context

        self.context.add_lib_client_factory("zhiyoufy_client", self.build_zhiyoufy_client)

    def build_zhiyoufy_client(self):
        if self._zhiyoufy_client is None:
            from zhiyoufy.clients.zhiyoufyclient.zhiyoufy_client import ZhiyoufyClient
            self._zhiyoufy_client = ZhiyoufyClient()
        return self._zhiyoufy_client        
```      

`cleanup`用于清理，比如账号logout

## case描述

case描述中有测试步骤列表，每一项指定一个测试步骤，它的前缀指定由哪个handler处理，比如下面的
步骤type是`zhiyoufy_environment_get_single_by_name`，它的前缀`zhiyoufy_`就是
`DynamicFlowZhiyoufyClient`所指定的，所以会转给它处理

```json
    {
        "type": "zhiyoufy_environment_get_single_by_name",
        "environment_var_path": "environment_var",
        "name": "{{ env_name }}",
        "must_exist": true,
        "step_description": "查找名字为name的environment"
    },
```
