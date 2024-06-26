
## 目录组织说明

所有proto文件会放在`protos`目录，然后下一级目录用来区分
大的软件，再下一级目录固定为`grpc`，这个是为了把grpc自动
生成的代码放到一起和手写的代码区分开

## add proto file

在protos目录下添加proto文件，比如

因为`routing.proto`文件里的java_package路径是
`com.cloudminds.harix.routing`，所以我们参考它创建
`protos/harix/grpc/routing`目录，然后把`routing.proto`
放到下面

## modify proto file

因为我们的目录组织结构和proto里写的不一致，主要是base路径不一致，
所以需要修改proto文件适应，比如修改

`import "common/common.proto";` 到 `import "harix/grpc/common/common.proto";`

## generate grpc code

修改`generate_pb.py`添加proto文件路径到对应列表，比如添加
`./protos/harix/grpc/routing/routing.proto`到`generate_for_harix`
中的列表 **请按照字母顺序添加，这样可以方便和文件系统对比哪个加了哪个没加**

执行`generate_pb.py`生成代码

## 添加client

### 参照服务提供方示例

比如`routing.proto`对应服务为`harix-routing-service`，下载下来后
首先安装依赖包，这样在IDE比如visual studio code中可以代码跳转，参照
项目里的`Dockerfile`可以知道用`go mod tidy`安装依赖

**具体go环境安装参考go官方文档**

学习研究示例比如`test\planning_test.go`

### 创建python侧client

新proto文件可能属于一个新应用，也可能属于一个应用的模块

以`routing.proto`为例，服务提供方反馈它是一个独立服务，所以我们
在`harix`package下创建`routingclient`package

具体client规则可参考framework_intro中的client文档

GrpcClient和其它Client的区别只是它会通过grpc生成的stub调用grpc服务，其它方面
并没有区别

## 创建DynamicFlowClient

比如针对`harix.routingclient`创建`harix.dynamicflow.DynamicFlowRoutingClient`，
并且在`harix.app.TestDynamicFlow`中注册

## 创建j2脚本验证基本功能

比如创建`data/templates/routing_test/simple_planning.json.j2`，然后调试验证
