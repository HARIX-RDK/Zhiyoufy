
`https://hub.docker.com/r/grafana/grafana`

`middleware\grafana`

参考`middleware\grafana\.env_ref`文件创建对应docker-compose对应.env文件，
配置变量`GF_SERVER_ROOT_URL`为实际地址

```
GF_SERVER_ROOT_URL=http://xx.xx.xx.xx:3000
```

