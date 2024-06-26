---
title: 使用框架开发
---

在框架介绍部分有介绍框架的不同部分，这里介绍如何基于框架开发一个具体的worker

如框架介绍中所说，开发涉及两部分，一个是**clients和handler封装**，一个是**case的封装**

## clients和handler封装

### 模拟rcu相关

`harix-auto-clients`这个repo是对需要的clients和handler的封装，比如

- `src/harixclients/harixclient`: 对模拟rcu的client封装
- `src/harixclients/dynamicflow/DynamicFlowHarixClient.py`: 对`harix_client_`type前缀的handler的封装

```python
class HarixClient(CustomLibraryBase):
    def __init__(self):

        self.asr_client = HarixClientAsr(parent_client=self)
        self.rcc_client = HarixClientRcc(parent_client=self)
        self.robot_client = HarixClientRobot(parent_client=self)
        self.switch_client = HarixClientSwitch(parent_client=self)
        self.tts_client = HarixClientTts(parent_client=self)
        self.vision_client = HarixClientVision(parent_client=self)
        self.push_client = HarixClientPush(parent_client=self)
        self.nlu_client = HarixClientNlu(parent_client=self)
        self.registry_client = HarixClientRegistry(parent_client=self)
        self.app_client = HarixClientApp(parent_client=self)
        self.world_server_client = HarixClientWorldServer(parent_client=self)
        self.notify_client = HarixClientNotify(parent_client=self)      
```

```python
class DynamicFlowHarixClient:

        self.type_prefix = "harix_client_"

    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.context = dynamic_flow_base_inst.context
        self.robot_logger = dynamic_flow_base_inst.robot_logger

        self.context.add_lib_client_factory("harix_client", self.build_harix_client)

    def cleanup(self, case_context):
        self.harix_client.switch_client.disconnect_if_needed()

    def build_harix_client(self):
        if self._harix_client is None:
            from harixclients.harixclient.HarixClient import HarixClient
            self._harix_client = HarixClient()
        return self._harix_client           
```

### smartmap相关

- `src/harixclients/mapclient`: 对smartmap的client封装
- `src/harixclients/dynamicflow/DynamicFlowSmartmap.py`: 对`smartmap_`type前缀的handler的封装

```python
class MapClient(CustomLibraryBase):
    def __init__(self):

        self.layer_client = MapClientLayer(parent_client=self)
        self.location_client = MapClientLocation(parent_client=self)
        self.map_base_client = MapClientMap(parent_client=self)
        self.virtual_wall_client = MapClientVirtualWall(parent_client=self)
        self.poi_client = MapClientPoi(parent_client=self)
        self.management_client = MapClientManagement(parent_client=self)
        self.stat_client = MapClientStatistics(parent_client=self)
        self.layer_rule_client = MapClientLayerRule(parent_client=self)
        self.version_client = MapClientMapVersion(parent_client=self)
        self.rcu_client = MapClientRCU(parent_client=self)
        self.signal_client = MapClientSignal(parent_client=self)
        self.area_client = MapClientArea(parent_client=self)
        self.path_client = MapClientPath(parent_client=self)
        self.mapbuild_client = MapClientMapBuild(parent_client=self)
        self.utility_client = MapClientUtils(parent_client=self)
        self.ue_client = MapClientUE(parent_client=self)
        self.grid_client = MapClientGrid(parent_client=self)
        self.rod_client = MapClientRod(parent_client=self)      
```

```python
class DynamicFlowSmartmapClient:
    def __init__(self):

        self.type_prefix = "smartmap_"

    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.context = dynamic_flow_base_inst.context
        self.robot_logger = dynamic_flow_base_inst.robot_logger

        self.context.add_lib_client_factory("smartmap_client", self.build_smartmap_client)

    def cleanup(self, case_context):
        pass

    def build_smartmap_client(self):
        if self._smartmap_client is None:
            from harixclients.mapclient.MapClient import MapClient
            self._smartmap_client = MapClient()
        return self._smartmap_client              
```

### handler列表

对于要参与自动化测试的每个功能模块，都设计一个相应的handler。   
比如，对于HARIX平台的自动化测试，根据测试需求，我们主要设计了下面这些handler: 

* `DynamicFlowHarixClient`: 测试HARIX云端功能  
* `DynamicFlowRocClient`: 测试ROC功能
* `DynamicFlowAutoPortalBackendClient`: 控制自动化模拟座席后端
* `DynamicFlowRcusimClient`：控制使用RcuSim模拟的机器人
* `DynamicFlowCmsProvision`：自动化操作CMS portal配置用户数据
* `DynamicFlowPortal`： 测试云端座席功能
* `DynamicFlowSmartmapClient`： 测试Smartmap功能
* `DynamicFlowSmartVoiceClient`： 自动化操作smartvoice portal配置用户数据

### 实现client

我们以`HarixClient`为例，来介绍clients组的实现

#### child clients

以CustomLibraryBridgeBase为父类，实现HarixClientBridge，作为所有child clients的父类。
按照云端业务实现下面这些child client：

* `HarixClientAsr`: 对应云端skill-asr模块功能
* `HarixClientVision`： 对应云端skill-vision模块功能
* `HarixClientNlu`：对应云端skill-nlu模块功能
* `HarixClientRcc`: 对应云端Rcc模块功能
* `HarixClientRegistry`: 对应云端Registry模块功能
* `HarixClientRobot`： 对应云端skill-robot模块功能
* `HarixClientSwitch`： 对应云端switch模块功能
* `HarixClientTts`: 对应云端skill-tts模块功能
* `HarixClientPush`: 对应云端MQTT server模块功能   

除了按照业务来设计child client之外，根据测试需求，有时我们还需要有一个业务管理模块, 用来把各个child client的行为关联管理起来。  
比如在HarixClient组内就有一个这样的child client    

* `HarixClientApp`: 它不与云端业务直接相关，负责将对应于云端各个业务的child client模拟的行为进行关联和管理。

#### parent client

以CustomLibraryBase为父类，实现HarixClient。  
HarixClient是上面所有child client的parent client, 在parent client初始化时，实例化所有的child clients。

```python
self.asr_client = HarixClientAsr(parent_client=self)
self.rcc_client = HarixClientRcc(parent_client=self)
self.robot_client = HarixClientRobot(parent_client=self)
self.switch_client = HarixClientSwitch(parent_client=self)
self.tts_client = HarixClientTts(parent_client=self)
self.vision_client = HarixClientVision(parent_client=self)
self.push_client = HarixClientPush(parent_client=self)
self.nlu_client = HarixClientNlu(parent_client=self)
self.registry_client = HarixClientRegistry(parent_client=self)
self.app_client = HarixClientApp(parent_client=self)
```

## case库

`harix-auto-test2`这个repo是对case的封装

### 实现GlobalLibraray

我们将`GlobalLibraryBase`作为父类实现了`harixautotest/app/GlobalLibrary.py`重写了父类的`init_default_config()`, 对HARIX平台自动化测试case中使用到的配置项进行缺省配置值初始化。
比如下面这个测试步骤中的"ignore"字段，就是和具体的测试case有关的配置项。

```json
    {
      "type": "harix_client_tts_synthesize",
      "text": "At cur_time, many efforts are made to improve the elders life",
      "vendor": "Microsoft",
      "lang": "th-TH",
      "name": "th-TH-Pattara",
      "expected_err_code": 0,
      "output_file": "microsoft_tts.mp3",
      "file_min_size": 1000,
      "max_delay": 5000,
      "step_description": [
        "调用harix-skill-tts的synthesize接口通过vendor Microsoft将文本转为语音文件，",
        "验证err_code, file_min_size, max_delay等"
       ],
      "ignore": {{ test_step_ignore.tts_vendor_microsoft | tojson() }}
    },
```

如果ignore的值为true,表示这个步骤不执行，直接跳过。它的值来自于`test_step_ignore.tts_vendor_microsoft`,我们可以在`GlobalLibrary`中对`test_step_ignore.tts_vendor_microsoft`进行缺省值配置。

```python
   def init_default_config(self):

        self.init_test_step_ignore()

    def init_test_step_ignore(self):
        test_step_ignore = self.config_inst.test_step_ignore      
        if "tts_vendor_microsoft" not in test_step_ignore:
            test_step_ignore["tts_vendor_microsoft"] = True
```

上面的code表示如果在配置文件中没有定义`test_step_ignore.tts_vendor_microsoft`,那么它的值就是True

### 使用GlobalLibraray

当在robot framework中使用时，需要在robot的resource文件中配置

```text title=cases/global_resource.robot
Library           harixautotest.app.GlobalLibrary  ${global_library_config}  WITH NAME    GlobalLibrary
```

当直接使用python进行调试时需要在python文件中配置

```python title=debug_case_harix_test.py
def debug_harix_test(cases_list):
    GlobalLibraryBase.g_global_library_base = None
    GlobalLibrary.g_global_library = None
    GlobalLibrary.GlobalLibrary("config/harix_test/robot.conf")
```

### 实现TestDynamicFlow

我们将`TestDynamicFlowBase`作为父类实现了`harixautotest/app/TestDynamicFlow.py`。  

`TestDynamicFlow`初始化时，挂载需要的handlers 

```python
        self.add_handler(DynamicFlowAutoPortalBackendClient())  
        self.add_handler(DynamicFlowCmsProvision()) 
        self.add_handler(DynamicFlowHarixClient())
        self.add_handler(DynamicFlowPortal())
        self.add_handler(DynamicFlowRcusimClient())
        self.add_handler(DynamicFlowRocClient())
        self.add_handler(DynamicFlowSmartmapClient())
        self.add_handler(DynamicFlowSmartVoiceClient())
```

### 使用TestDynamicFlow

当在robot framework中使用时，需要在robot的suite文件中配置

```text title=cases/harix_test/dynamic_flow_test/02030__portal_qa_using_smartvoice.robot
Resource          ../../global_resource.robot
Library           harixautotest.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags  nostat-portal_qa_using_smartvoice  smartvoice_cd

*** Test Cases ***
Test Portal QA Using SmartVoice
    TestDynamicFlow.run  dynamic_flows_from_tpl/dynamic_flow_test/portal_qa_using_smartvoice.json.j2
```

当直接使用python进行调试时需要在python文件中配置

```python title=debug_case_harix_test.py
def debug_harix_test(cases_list):

        test_dynamic_flow_inst = TestDynamicFlow()

        if isinstance(test_config, str):
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config)
        else:
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config[0], test_config[1])
```

### 设计case

按需设计，可参考框架介绍中的相关文档

