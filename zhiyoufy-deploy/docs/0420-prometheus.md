
## docker

`middleware\prometheus`

参照`middleware\prometheus\prometheus_ref.yml`创建`prometheus.yml`文件，
主要是修改scrape_configs，配置实际的ip，比如

```yaml
scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]
  - job_name: "zhiyoufygo"
    static_configs:
      - targets: ["10.11.102.212:9090"]
```

## helm

`middleware\prometheus-helm`

按需配置`server.persistentVolume.storageClass`，然后通过`install.sh`安装
安装后更新`server.service.nodePort`为实际分配的端口

