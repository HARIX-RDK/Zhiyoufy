import argparse
import sys
import time
import os

from zhiyoufy.common import init_app_base, zhiyoufy_context
from zhiyoufy.common.utils import RandomUtil, TimeUtil
from zhiyoufy.worker.app import WorkerApp
from zhiyoufy.worker.master import MasterChannelController, MasterChannelEventType


def console(cl_arguments):
    cl_args = handle_arguments(cl_arguments)

    main(cl_args.config_file)


def handle_arguments(cl_arguments):
    parser = argparse.ArgumentParser(description='')
    # Configuration files
    parser.add_argument('--config_file', '-c', type=str,
                        default="config/zhiyoufy_worker/zhiyoufy_worker.conf",
                        help="Config file (.conf) for zhiyoufy worker.")

    return parser.parse_args(cl_arguments)


def main(config_file):
    init_app_base.init_app_base(config_file, init_app_default_configs)

    global_context = zhiyoufy_context.get_global_context()
    global_context.elk_module_id = "zhiyoufy_worker"
    global_context.app_run_id = RandomUtil.gen_guid()
    global_context.app_start_timestamp = TimeUtil.get_current_time_isoformat()

    config_inst = zhiyoufy_context.get_config_inst()

    pod_name = os.environ.get('MY_POD_NAME')
    global_context.pod_name = pod_name
    if pod_name:
        config_inst.worker_app.worker_name += "_pod_%s" % pod_name

    master_channel_controller = MasterChannelController()
    worker_app = WorkerApp()

    master_channel_controller.start()
    worker_app.start()

    master_channel_controller.send_simple_event_to_handler(MasterChannelEventType.MASTER_CHANNEL_CONNECT_REQ)

    while True:
        time.sleep(10)


def init_app_default_configs():
    config_inst = zhiyoufy_context.get_config_inst()

    if not hasattr(config_inst, "private_config_path"):
        config_inst.private_config_path = []

    for config_path in config_inst.private_config_path:
        private_config_file = os.path.join(config_inst.root_dir, config_path)

        if not os.path.exists(private_config_file):
            raise Exception(f"config file {config_path} not exist")

    if not hasattr(config_inst, "project_dir"):
        if hasattr(config_inst, "project_dir_name"):
            config_inst.project_dir = os.path.join(config_inst.root_dir, config_inst.project_dir_name)
        else:
            config_inst.project_dir = os.path.join(config_inst.root_dir, "proj-zhiyoufy-worker")

    if not hasattr(config_inst, "worker_app"):
        raise Exception("Missing config for worker_app")

    for required_config in ["master_host", "master_addr"]:
        if not hasattr(config_inst, required_config):
            raise Exception(f"Missing config {required_config}")

    worker_app_config = config_inst.worker_app
    for required_config in ["worker_app_name", "worker_group", "group_token_name",
                            "group_token_secret", "worker_name", "max_active_job_num"]:
        if not hasattr(worker_app_config, required_config):
            raise Exception(f"Missing config {required_config} in config worker_app")


if __name__ == '__main__':
    console(sys.argv[1:])
