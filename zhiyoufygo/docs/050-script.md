
## 脚本格式

nodes与commands只能指定一个

- name: 脚本名字
- maxCommandSeq
- nodes
    + name: node名字，与parent路径合成构成当前node的路径
    + nodes or commands
- commands
- finallyNode

### block

类似于编程语言中的block，比如用`{}`包含的代码块
block支持嵌套
内置handler实现了if, for等block

### catch point

用于错误处理，当一个命令处理失败时，从所在block开始一层层block找
catch point，如果找到从那里开始继续运行，找不到则脚本失败

## handler

不同的handler实现不同命令的处理
每个handler处理指定前缀的命令，比如`pkg/basecmd/BaseCommandHandler`用于处理前缀为`base/`的命令

### RootCtx

当RootCtx.Done()时候代表脚本被请求提前结束，应该尽量利用这个提前结束，从而不block stop流程

## ScriptJobRunner

当StopRequested为true时跳转到finally处理
当执行到finally node时候切换RootCtx

- RegisterCommandHandler
- ParseScript
- RunScript
- NotifyCommandOk: 有需要跳转需求的command可以通过这个api设置next hop
- ScriptJobStatus: 状态，Status_Done, Status_Failed, Status_Stopped

## ScriptRunContext

贯穿整个脚本执行周期的context

- RootCtx: 用于提前结束
- finallyRootCtx: 用于finally node处理时候的提前结束，不过正常应等待finally模块实行结束以确保资源正确释放
- HttpClient: 代表单一用户的http client，不同用户使用不同的从而更真实模拟真实情况
- IConfigContext: 外部配置
- IVarContext: 储存变量，不同步骤的command可以通过这个传递信息
