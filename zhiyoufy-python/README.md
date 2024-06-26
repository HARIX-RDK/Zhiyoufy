# zhiyoufy-python

zhiyoufy python worker base

## Run

### local http server

```bash
python -m http.server 8500 --directory proj-zhiyoufy-worker
```

## package layout

<https://github.com/pypa/sampleproject>

### src

使用src目录可以方面同时支持测试installed和dev版本

<https://docs.pytest.org/en/7.1.x/explanation/goodpractices.html#tests-outside-application-code>

- Your tests can run against an installed version after executing pip install .
- Your tests can run against the local copy with an editable install after executing pip install --editable .

## pipenv

python venv环境通过pipenv管理，具体的install_requires还是在setup.py维护，只是
用过pipenv来创建测试环境

<https://pipenv.pypa.io/en/latest/basics/>

<https://pipenv.pypa.io/en/latest/advanced/>

```bash
pipenv install requests

pipenv install --dev pytest
```

### pycharm

在File -> settings -> Project Interpreter配置使用pipenv

设置default runner为pytest(设置后不起作用，但后来不知道咋pytest好使了，不知道是不是因为
在interpreter packages列表里点击pytest出来packages搜索对话框触发的)

<https://www.jetbrains.com/help/pycharm/pytest.html>

## pytest

测试通过pytest

<https://docs.pytest.org/en/7.1.x/getting-started.html>

<https://docs.pytest.org/en/7.1.x/explanation/goodpractices.html#test-discovery>

<https://docs.pytest.org/en/7.1.x/how-to/mark.html>

<https://docs.pytest.org/en/7.1.x/how-to/assert.html>

pytest will run all files of the form `test_*.py or *_test.py` in the current directory and its subdirectories.

```bash
pytest test_mod.py

pytest testing/

pytest test_mod.py::test_func

pytest test_mod.py::TestClass::test_method

pytest -m slow
```

## 依赖库

### websocket-client

- <https://websocket-client.readthedocs.io/en/latest/index.html>
- <https://github.com/websocket-client/websocket-client>

### stomper

- <https://pypi.org/project/stomper/>
- <https://github.com/oisinmulvihill/stomper>

### stomp.py

- <https://github.com/jasonrbriggs/stomp.py>
- <http://jasonrbriggs.github.io/stomp.py/api.html>
- <https://jasonrbriggs.github.io/stomp.py/stomp.html>

## 参考文档

<https://www.iana.org/assignments/websocket/websocket.xhtml#subprotocol-name>

