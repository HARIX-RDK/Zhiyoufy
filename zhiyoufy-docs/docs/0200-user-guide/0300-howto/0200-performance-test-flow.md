
基本流程与功能测试相同，下面主要描述不同点

- 点击run并配置并发等参数(`Run Num`与`Parallel Num`必须相等)
- 在active job runs标签下点击按钮`Perf`进入管理页面
- 在`Perf JobRunDialog`页面可以
    + 调整`PerfParallelNum`
    + 点击`DashboardAddr`打开对应dashboard页面
    + 通过`WorkerNames`知道被分配到了哪个worker，从而可以查看对应log