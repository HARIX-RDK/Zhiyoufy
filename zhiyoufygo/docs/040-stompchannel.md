
- 自动重连
- 发送消息通过channel
- 接收消息通过channel

## 状态与资源

- running: 代表doRun被启动
- runWg: 用于Stop同步
- rootCtxCancel: 用于Stop时候通知进行中工作

### channels

- eventChan: 运行时内部event loop channel

### events

- EventStateUpdate

### goroutine

- doRun: 处理event loop
    + runWg
- taskConnect: stomp over websocket连接
    + EventTaskConnectEnd
- readLoop: stomp subscription读取消息
    + activeReadLoop
    + eventReadLoopEnd

### timer

- reconnectTimer
    + reconnectTimerCancel
    + eventReconnectTimeout

### 其它资源

- wsConn: 底层websocket连接
