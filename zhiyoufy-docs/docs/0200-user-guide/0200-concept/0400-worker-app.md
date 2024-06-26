
具体的执行是由worker负责的，worker app用于代表worker可执行的job，这里是通过worker label区分的

## worker label设置

### app层级

一般和worker的代码能力相关，比如对应应用可以测试产品A, B，C

### worker group层级

用于区分不同的group，可能是由于网络相关，比如部署在stage环境中的worker与部署在dev环境的worker网络不相通

这个主要用于与env做匹配，比如stage环境和dev环境是网络不通的，当测试stage环境时我们需要
通过指定需要对应worker label来匹配对应的worker app实例

## worker group

用于区分worker app的运行实例，同一个worker group的实例应该是对等的

为了防止意外注册，worker group注册时需要指定token

## active workers

展示实时注册的worker，当前的容量等
