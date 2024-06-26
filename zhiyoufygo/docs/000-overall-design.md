
## IRunContext

不依赖于script，或者说IRunContext并不一定就是运行script时，比如可能是在
单元测试并且没有通过script

- Bus
- IBaseRunContext
- IConfigContext
- ILogContext
- IVarContext

### Bus

主要用于上层动态监听底层事件，而底层不关注具体谁监听，从而即解耦又有动态灵活

### IBaseRunContext

- `GetRootContext()`: 模拟用户的root context，每次调用传递的context都应该以这个为root，
    从而可以中途停止，比如动态调整并发数时
- `GetHttpClient()`: 每一路代表一个用户，可以共用一个`http.Client`，不同路应该使用不同的

### IConfigContext

运行配置相关，运行后通常不再改变

### ILogContext

- Tag: 大量并发时候，用于区分某一路
- LogLevelThreshold: 大量并发时候不可能都输出log，因此通过给不同log context设置不同阈值从而让部分
能完整输出log，部分只是输出非常严重的log

### IVarContext

不同的模块间从解耦的角度考虑最好不要知道其它模块，如果只是需要某个信息，那么可以通过IVarContext来中转，
从而各个模块只是单一和IVarContext通信

## client

代表某个模块，创建时应传递IRunContext，运行时可以从IRunContext获取config，
写入var等

## script

script层和rest controller层对等，不同的时它描述了一系列的操作，同时因为它处于高层，
所以底层比如client层不应该知道它的存在

## rest controller

单独使用rest controller层时候可以用来创建长期活跃的模拟用户，或者把类似script的功能在
外部实现，比如在python侧，如果在python侧实现则更强调灵活性，go侧的script则更强调性能测试。
