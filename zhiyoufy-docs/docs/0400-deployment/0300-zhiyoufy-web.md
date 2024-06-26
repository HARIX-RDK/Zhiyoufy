# Zhiyoufy网页前端

## nginx

`middleware\nginx_server`

- 拷贝nginx_server目录到服务器
- docker-compose启动

## zhiyoufy-web

- 把前端build出来的文件放到nginx服务的地址，参考`middleware\nginx_server\docker-compose.yml`,
缺省是`/srv/zhiyoufy-web`，这个地址同时也配置在`middleware\nginx_server\config\docserver.conf`里，
如果要修改需要保持一致
