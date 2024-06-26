---
title: TestDynamicFlowBase
---

## 类插件挂载handler

每个handler负责处理特定type_prefix开头的步骤

```python
    def add_handler(self, handler):
        if handler.type_prefix in self.handler_map:
            raise Exception("%s is already registered" % handler.type_prefix)

        self.handler_map[handler.type_prefix] = handler
        handler.setup(self)
```        

比如下面的GeneralCommandHandler用于处理`general_`开头的步骤

```python
class GeneralCommandHandler(CommandHandlerBase):
    def __init__(self, dynamic_flow_base_inst):
        super().__init__(dynamic_flow_base_inst)

        self.type_prefix = "general_"
```

## 模板渲染

case描述文件中有一些动态部分，比如某个步骤是否需要ignore，某个变量的值等，这里负责
通过解析的config及配置的**override_map**来渲染

我们使用[jinja][]模板文件来描述case，模板的变量定义在配置文件中，比如等待收到期望的响应的最大时长，某个操作的重复次数等，渲染后生成最终的case描述文件。

### 模板变量替换

有些case步骤基本一样，只是具体配置项不一样，这种我们通过共享case描述文件，然后通过
**override_map**来对不同case替换不同配置项来做渲染前的预处理

```python
    @staticmethod
    def render_config_if_needed(config, render_context=None, dst_path=None, override_map=None):

            for orig_value, override_value in override_map.items():

                    base_str = base_str.replace(orig_value, override_value)                    
```

### 模板具体渲染

```python
    @staticmethod
    def render_config_if_needed(config, render_context=None, dst_path=None, override_map=None):

        template_library = TemplateLibrary()
        template_library.render(base_str, render_context, dst_path)      
```

## case运行

### 执行测试步骤

依次读取渲染后的文件中的每个*test step*，根据step描述中**type**的前缀,来找到相应的handler来处理， 比如

```json
    {
        "type": "zhiyoufy_environment_get_single_by_name",
        "environment_var_path": "environment_var",
        "name": "{{ env_name }}",
        "must_exist": true,
        "step_description": "查找名字为name的environment"
    },
```

`zhiyoufy_environment_get_single_by_name`的前缀是`zhiyoufy_`，这个是`DynamicFlowZhiyoufyClient`这个handler注册处理的，所以会转给它处理

```python
class DynamicFlowZhiyoufyClient:
    def __init__(self):

        self.type_prefix = "zhiyoufy_"

    def process_type_of_zhiyoufy_environment(self, data):

        elif data['type'] == 'zhiyoufy_environment_get_single_by_name':
            self.process_type_zhiyoufy_environment_get_single_by_name(data)      
```

### cleanup

在所有的测试步骤都执行完毕，或者因为某个步骤执行失败导致测试异常结束时，需要做清理，比如资源释放，
清理分两部分，一个是case描述文件中指定的`finally`部分，一个各个handler自己的`cleanup`

- case描述文件中指定的`finally`部分: 比如有些步骤创建了一些临时资源，可以在finally里检查存在则删除
- 各个handler自己的`cleanup`: 通常是更一般的清理，比如账号的logout

```python
    def run(self, test_config, override_map=None):

                self.process_finally_node()

                    for _, handler in self.handler_map.items():
                        handler.cleanup(self.case_context)                
```    

[HOCON]: https://github.com/lightbend/config/blob/main/HOCON.md
[RobotFramework]: https://robotframework.org
[pyhocon]: https://github.com/chimpler/pyhocon
[jinja]: https://jinja.palletsprojects.com/en/2.10.x/
