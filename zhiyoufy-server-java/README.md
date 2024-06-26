# zhiyoufy

一个帮助产品zhiyou(质优)的框架。

## flyway

### migrate in console

可用于单纯调试数据库schema时，先用一个可编译版本
通过migrate更新数据库，然后在当前工作版本通过mbg
来更新生成代码

直接使用当前工作版本migrate的话，一旦mbg更新可能导致
编译失败，进而影响mvn运行migrate

```bash
cd zhiyoufy-server
mvn -Dflyway.configFiles=flyway.conf flyway:migrate
```

## mapstruct

可用于高效mapping，比如从数据库Entity到DTO

没有配置spring DI，因为在UT时候autowire会报错.

## caffeine

本地缓存，主要使用expireAfterAccess能力

## 数据库变动流程

- 编写数据库升级脚本: zhiyoufy-server/src/main/resources/db/migration/mysql
- 创建一个新数据库调试用，导入备份数据 
- 使用flyway更新数据库
  + zhiyoufy-server/flyway.conf
  + 参考前面mvn命令
- 使用mbg生成对应java文件
  + zhiyoufy-mbg/src/main/resources/generator.properties
  + zhiyoufy-mbg/src/main/java/com/example/zhiyoufy/mbg/Generator.java
- 如果涉及mapstruct，则删除target重新生成  
- 设计实现对应dao: zhiyoufy-server/src/main/java/com/example/zhiyoufy/server/dao
- 设计实现对应service: zhiyoufy-server/src/main/java/com/example/zhiyoufy/server/service

## 数据库备份调试

Server -> Data Export对数据备份
创建新的schema
Server -> Data Import导入数据到新建schema
给用户赋权允许访问新建schema

```sql
GRANT ALL
  ON zhiyoufy_20240227.*
  TO 'zhiyoufy'@'%'
  WITH GRANT OPTION;
```
