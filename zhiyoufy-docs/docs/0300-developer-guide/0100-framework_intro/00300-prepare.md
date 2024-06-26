
`debug_case_zhiyoufy_test.py`

## environment

`zhiyoufy_prepare/prepare_environments.json.j2`

在`config/zhiyoufy_test/private_params/env.conf`中引入目标environment配置，比如

```text
include "../../sample_envs/env_aaa/zhiyoufy_environment.conf"
```

## config single

`zhiyoufy_prepare/proj_PROJECT_NAME_config_single/TEST_GROUP_NAME.json.j2`

## config collection

`zhiyoufy_prepare/proj_PROJECT_NAME_config_collection/TEST_GROUP_NAME.json.j2`

## template

`zhiyoufy_prepare/proj_PROJECT_NAME_templates/TEST_GROUP_NAME.json.j2`
