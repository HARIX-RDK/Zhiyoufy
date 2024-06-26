---
title: Client
---

## 介绍

client是类似sdk client的对目标产品的client侧封装，具体实现上为了降低复杂度，也为了
维持与后端的一致，一般都是由一个parent client和一组child clients组成

同时一般SDK的接口是传递多个参数，而这里为了方便通过json传递参数把接口的输入
都统一成接收一个dict，比如

```python
    def config_collection_del(self, params):
```

此外一般SDK是直接写代码来串流程，而这里是通过json文件来串流程，所以
会在参数`params`里设置变量路径，比如下面的`user_var_path`

```json
    {
        "type": "zhiyoufy_user_get_single_by_name",
        "user_var_path": "target_user",
        "username": "{{ zhiyoufy.user_params_group_2.username }}",
        "must_exist": true,
        "step_description": "查询指定名字的user"
    },
```

## 模块化实现

为了控制复杂度，同时对应服务一般也是模块化的，client的实现会

* 对应一组clients
  + 一个parent client
  + 多个child clients
* parent client负责实例化每个child client
* 根据业务功能或者接口定义划分不同的child client
* 各个client接口只做必要基本的工作，以增加复用性
* 为了降低耦合，确定全局可见的数据可以放在parent client然后通过bridge访问，其它的应该尽量通过Global Run-Time Data实现信息传递
* 根据需求，child clients之间可以有交互，即一个child client可以调用另一个child client的函数，不过child clients间也应尽量通过step_var方式传递信息，从而减少耦合
* child clients之间通过bridge关联，同时也可以通过bridge访问parent暴露的其它信息

```python
        self.user_client = ZhiyoufyClientUser(parent_client=self)
        self.environment_client = ZhiyoufyClientEnvironment(parent_client=self)
        self.config_single_client = ZhiyoufyClientConfigSingle(parent_client=self)
        self.config_collection_client = ZhiyoufyClientConfigCollection(parent_client=self)
```

## 接口设计原则

类似DDD里讲的领域语言，client的接口应该与服务提供方的接口或者功能是一致的，比如服务提供方有一个获取资源列表的接口，然后当前业务需要获取其中一个资源的id信息，那么client的接口应该封装两个接口，一个接口用于获取资源列表，一个接口处理获取资源列表返回的结果提取需要的信息，换句话说服务提供方的接口应与client接口是一一对应的，同一个服务方接口不应该在不同client接口中重复实现。为了达到这个效果，它不能少干，这样就不能满足业务需求，也不能多干，这样会影响复用和可读性。

服务提供方的接口如果封装了，就应该是满足json输入的格式，这样内部和json串流程可以共用一套实现。

## parent基类CustomLibraryBase

CustomLibraryBase负责封装client基本数据和功能，比如

- config_inst
- global_context
- robot_logger
- `get_step_var(self, step_var_path, raise_exception_if_miss=None)`
- `set_step_var(self, step_var_path, step_var_value)`

## 配置数据

`CustomLibrarayBase`获取到了`GlobalLibraryBase`中保存的配置数据，可以被所有child clients使用，比如下面通过config_inst访问norm_timeout这个配置项

```python
timeout = params.get("timeout", self.config_inst.norm_timeout)
```

## clients组内信息共享传递

使用`local_context`在具有相同parent的child clients间进行信息传递和共享

## 全局信息共享传递

提供访问/获取Global Run-time Data的接口，来实现流程不同步骤间的信息传递和共享，比如下面`zhiyoufy_project_get_single_by_name`这一步会把信息存储到`project_var_path`指定变量，然后后面步骤可以通过指定对应变量路径获取

```json
    {
        "type": "zhiyoufy_project_get_single_by_name",
        "project_var_path": "project_var",
        "name": "zhiyoufy-auto-test",
        "step_description": "查找名字为name的project"
    },
    {
        "type": "zhiyoufy_project_create_if_missing_update_if_requested",
        "project_var_path": "project_var",
        "name": "zhiyoufy-auto-test",
        "description": "对应zhiyoufy这个git repo",
        "step_description": "查找指定name的project, 如果不存在就创建，存在则更新"
    },
```

## bridge基类CustomLibraryBridgeBase

它拥有一个parent client的实例，各个child client可以通过parent client来实现数据和信息共享传递，比如下面通过bridge访问user_client

```python
    def __init__(self, parent):
        self.log_prefix = "%s: " % type(self).__name__

        self.parent = parent

    @property
    def user_client(self):
        return self.parent_client.user_client        
```

## 实现举例

假定我们要对模块*newmodule*实现clients组
1. 实现类`NewmoduleClientBridge`，其父类为`CustomLibraryBridgeBase`,作为所有child clients的父类
2. 根据业务划分，实现两个child clients  
   * child A 
   > 实现类`NewmoduleClientChildA`, 其父类为`NewmoduleClientBridge`  
   * child B   
   > 实现类`NewmoduleClientChildB`, 其父类为`NewmoduleClientBridge`  
3. 实现parent client  
   实现类`NewmoduleClient`, 其父类为`CustomLibraryBase`, 并实例化所有child clients  
   > self.childAClient = NewmoduleClientChildA(parent_client=self)   
   > self.childBClient = NewmoduleClientChildB(parent_client=self) 

可以参照`zhiyoufy-python`里对`zhiyoufy`的封装`zhiyoufy.clients.zhiyoufyclient`

