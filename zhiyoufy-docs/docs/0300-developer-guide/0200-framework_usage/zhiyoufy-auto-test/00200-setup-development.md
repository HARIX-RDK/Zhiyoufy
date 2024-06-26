---
title:  搭建开发环境
---

## 下载代码

- 下载`harix-auto-test2`
- 创建`devdeps`目录，这个目录在`.gitignore`中被配置
- 在`devdeps`目录下下载依赖项，比如`zhiyoufy-python`, `harix-auto-clients`

## 创建python虚拟环境

- 创建虚拟环境，使用pipenv相比直接用venv的好处是
结合pyenv可以支持不同python版本，比如`pipenv --python 3.9
- 用editable模式安装从而可以改动直接生效

```bash
pipenv install --dev
pipenv shell
cd devdeps/zhiyoufy-python
pip install -e .
cd ../..
cd devdeps/harix-auto-clients
pip install -e .
```
