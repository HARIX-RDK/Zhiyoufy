
## 状态与资源

- running: 代表doRun被启动
- runWg: 用于Stop同步
- rootCtxCancel: 用于Stop时候通知进行中工作

### channels

- eventChan: 运行时内部event loop channel

### events

- jms.EventJobFinishData

### goroutine

- doRun: 处理event loop
    + runWg
- taskReadStompMessage
    + EventInStompMessage
    + EventTaskReadStompMessageEnd
- taskStopChannelController
    + EventTaskStopChannelControllerEnd
- taskReadChannelEvent
    + EventChannelEvent
    + TaskReadChannelEventEnd

### timer

### registerManager
    
- registerTimer

### jobManager
    
- jobRunner

### 其它资源

- channelController: stomp channel controller
- stompConn: 代表最新获取的stompConn
