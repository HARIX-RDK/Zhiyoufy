# Zhiyoufy主服务

准备一台安装了ubuntu的服务器

为方便管理，将部署相关的文件放到一个目录中，比如`/srv`

## mysql

### mysql部署

[https://hub.docker.com/_/mysql](https://hub.docker.com/_/mysql)

- 创建`/srv/mysql`目录
- 拷贝`middleware/mysql/docker-compose.yml`到这个目录
- 修改`MYSQL_ROOT_PASSWORD`的配置
- 按需修改volumes和port映射
- `docker-compose up -d`启动
- MySQL Workbench 确认连接正常
- 创建数据库，用户等，可参考下面示例，按需修改

```sql
CREATE DATABASE zhiyoufy_20240625;

CREATE USER 'zhiyoufy_user'@'%' IDENTIFIED BY 'zhiyoufy_password';

GRANT ALL PRIVILEGES ON zhiyoufy_20240625.* TO 'zhiyoufy_user'@'%';
```

:::warning

如果要在一台机器上起多个实例的话，`docker-compose.yml`的上级目录名字
不能一样，因为生成的container的名字前缀是上级目录名，起多份后起的就把前面
的冲掉了，所以比如起第二个可以用目录`/srv2/mysql2`

:::

### 备份

- MySQL Workbench
- 菜单 Server -> Data Export
- 选择目标数据库，点击Start Export

### 数据恢复

- MySQL Workbench
- 菜单 Server -> Data Import
- 选择数据文件，选择目标数据库，点击Start Import

## elk

### elk部署

[docker-elk][]

- 首先从[docker-elk][]下载代码，需要用分支`release-7.x`，因为用的spring boot版本有些旧，只支持7.X
- 修改`elasticsearch\config\elasticsearch.yml`使用basic license
```yaml
xpack.license.self_generated.type: basic
```
- 创建目录`/srv/elk/elasticsearch`, 修改`docker-compose.yml`使用本地目录做数据目录
```yaml
      ES_JAVA_OPTS: "-Xmx2048m -Xms2048m"

    volumes:
      - ./elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml:ro,Z
      - /srv/elk/elasticsearch:/usr/share/elasticsearch/data:Z
```
- 因为不需要logstash，所以从`docker-compose.yml`中删除掉
- 按照Readme安装配置
```bash
docker-compose up setup

docker-compose up -d

docker-compose exec -T elasticsearch bin/elasticsearch-setup-passwords auto --batch

# 更新.env文件
docker-compose restart
```

[docker-elk]: https://github.com/deviantony/docker-elk

## openjdk:11-jre

最终zhiyoufy需要运行在一个jre环境中，这里我们选用openjdk，另外为了方便调试，
我们重新生成了一个image预装了一些工具

- `misc\base-images\openjdk-11-jre`

## zhiyoufy-server

### 本地调试

- 配置`zhiyoufy-server/src/main/resources/local.yml`
- 启动后端
- 启动前端验证常用功能 (`vite.config.ts`里缺省配置开发模式时后端地址为`http://localhost:8088`)

### docker部署

- 先在桌面跑单元测试，确保测试通过
- 因为网络vpn原因，我们在生成docker image时候会跳过单元测试
- 按需生成或更新base image，这个原因是项目用到很多的依赖包，如果每次打包都重新下载太慢，
所以我们创建一个base image，从而生成最终image时候不用再次下载，所以如果依赖没有变就不需要更新
```bash
./build_docker/build_base.sh 20240625
```
- 生成最终image
```bash
./build_docker/build.sh 20240625
```
- 部署`zhiyoufy\docker-compose\docker-compose.yml`，按需设置image tag，配置文件等
