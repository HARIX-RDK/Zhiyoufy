# 架构

为了支撑zhiyoufy设计的功能，整个zhiyoufy分为以下几个子模块

![zhiyoufy-architecture](./../files/zhiyoufy-architecture.png)

## zhiyoufy-server-java

zhiyoufy-server-java是后端服务，用于管理用户，环境，测试运行等，它的主要
内部功能系统如下

- ums: 用户管理系统，支持添加，修改，删除用户，role分配
- ems: 环境管理系统，管理environment, config collection, config single
- pms: 项目管理系统，管理project, job template, template folder
- wms: worker管理系统，管理worker app, worker group, group token, active worker
- jms: job管理系统，管理job运行, cron schedule

## zhiyoufy-web

zhiyoufy-web是zhiyoufy-server-java对应的web前端，用户可以通过它执行对应功能，CICD
系统可以参考它对接

## zhiyoufy-python

zhiyoufy-python是用于功能测试的worker基础库，建议的使用方式是开发者再创建两大类库，一个是
client封装库，类似SDK库，一个是用例库

## zhiyoufygo

zhiyoufygo是用于性能测试的worker基础库，与zhiyoufy-python类似，也是建议的使用方式是开发者再创建两大类库，一个是
client封装库，类似SDK库，一个是用例库

## zhiyoufy-deploy

zhiyoufy-deploy是部署库，因为整个产品用到了数据库，消息中间件，grafana等，所以把对应的部署文件集中到这里
