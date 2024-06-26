
## jms

- `cmd\run.go`
- `pkg\cmd\run\run.go`
- `configs\zhiyoufygo.local.json`

- `zhiyoufy-deploy/middleware/prometheus/prometheus.yml`

```bash
./zhiyoufygo.exe
```

## worker

- `cmd\worker.go`
- `pkg\cmd\worker\worker.go`
- `configs\zhiyoufygo.local.json`

```bash
./zhiyoufygo.exe worker
```

`zhiyoufy-web`选择job template触发，然后进到Active Job Runs，点击Perf
- 调整PerfParallelNum
- 跳转到grafana
