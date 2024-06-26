
## intro

有的终端不能启动server监听，从而不能做为server对外提供rpc服务
(比如处于安全考虑，比如browser侧)

那也就是说要和终端通信需要终端主动发起连接，因为我们需要远端自动化服务能
主动发送消息给终端，所以需要是一个长连接，那么已有的协议有grpc和websocket，
grpc比较重，并且在browser上也没法跑，所以我们选择使用websocket。

grpc是一个完整的rpc方案，而websocket则只是一个底层通信机制，基于websocket上实现
rpc还需要选择一个具体的协议，已有的有stomp, json-rpc等，这里我们选择stomp，因为
它和http比较接近，很好理解，也是分header和body，然后还可以通过stomp server比如
rabbitmq做路由。

## stomp

[https://stomp.github.io/stomp-specification-1.2.html](https://stomp.github.io/stomp-specification-1.2.html)

## stomp rpc

### 监听地址

为了灵活，终端不直接连接到自动化服务器，而是通过一个stomp broker中转，怎么区分具体是发给
哪个终端的则是通过每个终端subscribe专属自己的topic来实现的，比如类似下面代码，一个终端分配
一个唯一的controllerId

```ts
    const subscription = this.stompClient.subscribe(
        `/topic/stomp-controller-${this.controllerId}`, this.onControllerMessage.bind(this));
```

### 自定义header

这里我们定义了几个消息header，具体意义如下

- appDestination: 代表本地应用层地址
- replyDestination: 代表消息响应目标地址
- replyAppDestination: 代表消息响应目的应用层地址
- streamId: 消息流标识，类似gprc的stream id，用于区分不同rpc调用

### normal flow

- 发送消息方配置消息头`appDestination`, `streamId`, `replyDestination`和`replyAppDestination`
- 然后发送到目标接收方对应的topic地址
- 接收方收到消息后按照`appDestination`路由到对应的处理函数
- 处理函数按照`replyDestination`和`replyAppDestination`回复响应

### appDestination约定

因为大家都比较熟悉rest路径，所以这里也约定采用类似路径规则，也就是`aaa/bbb/ccc`这种，
另外为了不冲突，约定第一级的路径名`system`用于系统层通信而不能用于应用

### ping pong

因为这个通道不是直连的，所以为了验证通道是通的，所有相关方都应该实现标准的ping pong逻辑，
这样可以通过这个验证通道正常，具体路径`system/ping`，`system/ping-rsp`
