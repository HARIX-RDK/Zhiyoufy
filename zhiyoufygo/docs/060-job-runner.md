
## feature

- ParallelNum: 并行运行数
- ScriptFile: 脚本路径
- ScriptContent: 脚本内容，与ScriptFile互斥
- CfgMap: json配置，脚本里的command可以从配置中提取信息
- ScriptRunnerCustomizer: 主要是注册command handler，从而能支持目标command
- LogGroupSize: 用于控制log的量，每LogGroupSize个运行的第一个会允许输出info级别log，其它则只能输出error级别

### spawn

- SpawnIntervalMs: 启动新batch运行间隔
- SpawnNum: 每次SpawnIntervalMs间隔后启动的数量

- stopping
- finishEventSent
- taskChildJobRunMap

- spawnTimer
    + eventSpawnTimeout
- spawnTimerCancel
- spawnedNum
- dynamicParallelNum: 缺省为false，此时所有运行结束后大的JobRunner就结束了，如果运行时收到ParallemNum调整命令
    则设置为true，此时如果一个运行结束则启动一个新的以维持ParallelNum个并行，整个JobRunner结束需要用户发送命令Stop

## 接口

- Bus

- Run
- Stop
- SendEvent
    + 调整ParallelNum

## 状态与资源

- running: 代表doRun被启动
- runWg: 用于Stop同步
- rootCtxCancel: 用于Stop时候通知进行中工作

### channels

- eventChan: 运行时内部event loop channel

### events

- EventJobFinishData

### goroutine

- doRun: 处理event loop
    + runWg
- taskChildJobRun

### timers

- spawnTimer
