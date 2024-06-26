# 部署介绍

整个部署相关的文件在**zhiyoufy-deploy**目录

单纯的python worker可以独立调试和运行

如果需要通过网站运行或者与CICD集成，则需要部署

zhiyoufy-server因为是单实例的，所以用docker-compose部署

worker推荐用k8s部署，因为k8s起多实例很方便
