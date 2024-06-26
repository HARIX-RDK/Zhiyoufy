
## RED

性能测试测试点与功能测试不同，可参考下面文章中提到的RED原则

[https://grafana.com/blog/2018/08/02/the-red-method-how-to-instrument-your-services/](https://grafana.com/blog/2018/08/02/the-red-method-how-to-instrument-your-services/)

- Rate (the number of requests per second)
- Errors (the number of those requests that are failing)
- Duration (the amount of time those requests take)

The RED Method is a good proxy to **how happy your customers** will be

## job template

因为性能测试是通过golang实现的，具体实现的不同导致`job template`的配置信息不同

### 性能测试特定配置

- IsPerf: 配置为true指定为性能测试
- DashboardAddr: 配置为对应用例的dashboard地址，方便用户跳转
- Run时候`Run Num`与`Parallel Num`必须相等

## dashboard

dashboard用于展示服务性能，对应前面的`RED`
