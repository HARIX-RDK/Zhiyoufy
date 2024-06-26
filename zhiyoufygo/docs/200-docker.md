
- `https://github.com/docker-library/buildpack-deps/blob/master/debian/bookworm/curl/Dockerfile`
- `https://github.com/docker-library/buildpack-deps/blob/master/debian/bookworm/scm/Dockerfile`
- `https://github.com/docker-library/golang/blob/master/1.21/bookworm/Dockerfile`

## base image

目的是下载依赖包，这样如果在没有新增依赖的情况下，后续编译不需要访问网络

`build_docker\Dockerfile.base`

```bash
./zhiyoufygo/build_docker/build_base.sh 20240204
```

## zhiyoufygo

```bash
chmod 755 ./zhiyoufygo/build_docker/*.sh
chmod 755 ./zhiyoufygo/bin/*

./zhiyoufygo/build_docker/build.sh 20240204
```
