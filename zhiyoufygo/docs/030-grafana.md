
## import dashboard

- Menu Home/Dashboards
- 点击New Import
- 选择dashboard对应json文件，比如docs下的JsonPlaceholder dashboard示例文件

## create dashboard

### dashboard setting

- General: 配置Shared Tooltip
- variables
    + rateInterval: 30s,1m,2m,5m

### row

添加row来组织，比如

- row zhiyoufy: 用来展示当前zhiyoufy并发路数，工作机cpu占用等

row的最右侧有8个小点，可以拽动来调整顺序

### visulization

左下方query那里选code，也就是直接输入prometheus公式

- Virtual Users: 多少路并发
- CPU Usage: 工作机负载
- Success Rate: 成功率
- Request Active: 当前进行中api
- Request Volume Total: RPS，请求处理效率
- Request Duration %90
- Request Duration Average

## export dashboard

<https://grafana.com/docs/grafana/latest/dashboards/share-dashboards-panels/#export-a-dashboard-as-json>
