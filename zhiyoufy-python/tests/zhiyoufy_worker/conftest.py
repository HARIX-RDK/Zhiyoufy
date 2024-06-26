import os

import pytest

from zhiyoufy.common import init_app_base, zhiyoufy_context
from zhiyoufy.worker import init_app_default_configs


@pytest.fixture
def init_app():
    tests_dir = os.path.dirname(__file__)
    config_file = os.path.join(tests_dir, "run_zhiyoufy_worker.conf")
    init_app_base.init_app_base(config_file, init_app_default_configs)

    global_context = zhiyoufy_context.get_global_context()
    global_context.elk_module_id = "zhiyoufy_worker"


@pytest.fixture
def config_inst(init_app):
    return zhiyoufy_context.get_config_inst()

