
压测时候需要大量worker，可以通过k8s环境来启动，部署时候可以通过helm

## 资源

### image-pull-secret

拉取image的鉴权信息

`.Values.imageCredentials`

### pvc

用于存储log，缓存文件等

`.Values.persistence`

`{{ template "zhiyoufygo-worker.fullname" . }}-worker-output`

### nginx-configmap

nginx配置文件

`{{ template "zhiyoufygo-worker.fullname" . }}-nginx-configuration`

配置`/zhiyoufy-worker/`对应本地地址`/srv/zhiyoufy-worker/`

```text
          location /zhiyoufy-worker/ {
              root   /srv;
              autoindex on;
          }
```          

### nginx-deployment

部署nginx，用于对外暴露log

`checksum/config`用于控制当config改变时触发pod重启

绑定pvc到路径`/srv/zhiyoufy-worker`，mount nginx配置文件

```yaml
            {{- if .Values.nginx.configurationFiles }}
            - mountPath: /opt/bitnami/nginx/conf/server_blocks
              name: configurations
            {{- end }}
            - mountPath: /srv/zhiyoufy-worker
              name: worker-output
```              

### nginx-service

将nginx端口对外暴露，如果不能预分配nodePort，可以先不配置`.Values.nginx.service.nodePort`，
然后等系统自动分配nodePort后再配置上从而固定

### worker-configmap

`{{ template "zhiyoufygo-worker.fullname" . }}-configuration`

worker的配置文件，比如`zhiyoufygo.json`

### worker-deployment

- `checksum/config`: 用于当配置改变时触发pod重启
- `MY_POD_NAME`: 把pod名字传进去从而创建独立的worker name
- `nodeSelector`: 限制pod部署的node
    + <https://kubernetes.io/docs/tasks/configure-pod-container/assign-pods-nodes/>
- `affinity`: 限制pod部署，为了防止worker消耗带宽比较多，可以通过这个限制尽量部署到不同的node上
    + <https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/>