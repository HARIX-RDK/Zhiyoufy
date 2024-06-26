import os
from datetime import datetime
import logging

from robot.output.pyloggingconf import RobotHandler

from .. import config as _config
from .. import init_app_base
from .. import zhiyoufy_context
from ..utils import MailUtil, IOUtil

__all__ = ["get_global_library_base", "GlobalLibraryBase"]

g_global_library_base = None


def get_global_library_base():
    global g_global_library_base
    return g_global_library_base


class GlobalLibraryBase:
    def __init__(self, config_file):
        global g_global_library_base

        if g_global_library_base is not None:
            raise Exception("g_global_library_base is not None, GlobalLibraryBase should only be created once")

        g_global_library_base = self

        init_app_base.init_app_base(config_file)

        self._normal_logger = zhiyoufy_context.get_normal_logger()
        self._robot_logger = zhiyoufy_context.get_robot_logger()

        config_inst = zhiyoufy_context.get_config_inst()
        self.config_inst = config_inst

        self.init_default_config_base()
        self.init_default_config()

        begin_time = datetime.now().strftime("%Y%m%d_%H%M%S")
        run_output_dir = os.path.join(config_inst.output_dir, begin_time)
        IOUtil.maybe_make_dir(run_output_dir)
        config_inst.run_output_dir = run_output_dir

        IOUtil.rm_old(config_inst.output_dir, config_inst.rm_old_days)

        if "mail_password" in config_inst and config_inst.mail_password:
            self.mail_util = MailUtil(config_inst.mail_server, config_inst.mail_user,
                                      config_inst.mail_password, config_inst.mail_user)
        else:
            self.mail_util = None

        self.global_context = zhiyoufy_context.get_global_context()
        self.step_vars_map = {}

    @staticmethod
    def _preprocess_step_var_path(step_var_path, cur_ns):
        var_paths = step_var_path.split(".")
        for i in range(len(var_paths) - 1):
            if var_paths[i] not in cur_ns or cur_ns[var_paths[i]] is None:
                cur_ns[var_paths[i]] = {}
            cur_ns = cur_ns[var_paths[i]]
        return var_paths[-1], cur_ns

    def get_step_var(self, step_var_path, raise_exception_if_miss=True):
        cur_ns = self.step_vars_map
        basename, cur_ns = self._preprocess_step_var_path(step_var_path, cur_ns)
        if cur_ns and basename in cur_ns:
            return cur_ns[basename]
        if raise_exception_if_miss:
            raise Exception("get_step_var: %s doesn't exist" % step_var_path)
        return None

    def set_step_var(self, step_var_path, step_var_value):
        cur_ns = self.step_vars_map
        basename, cur_ns = self._preprocess_step_var_path(step_var_path, cur_ns)
        cur_ns[basename] = step_var_value

    # sub class could override to init based on its need
    def init_default_config(self):
        pass

    def init_default_config_base(self):
        if "child_job_idx" not in self.config_inst:
            self.config_inst.child_job_idx = 0

        if "rm_old_days" not in self.config_inst:
            self.config_inst.rm_old_days = 30

        self.init_test_step_ignore_base()
        self.init_test_step_dynamic_base()
        self.init_test_case_ignore_base()

    def init_test_step_ignore_base(self):
        config_inst = self.config_inst

        if "test_step_ignore" not in config_inst:
            config_inst["test_step_ignore"] = _config.Params()

    def init_test_step_dynamic_base(self):
        config_inst = self.config_inst

        if "test_step_dynamic" not in config_inst:
            config_inst["test_step_dynamic"] = _config.Params()

    def init_test_case_ignore_base(self):
        config_inst = self.config_inst

        if "test_case_ignore" not in config_inst:
            config_inst["test_case_ignore"] = _config.Params()

    def setup_normal_logger(self):
        normal_logger = logging.getLogger("normal")

        self._normal_logger = normal_logger

    def setup_robot_logger(self):
        robot_logger = logging.getLogger("robot")
        robot_handler = RobotHandler()
        robot_logger.addHandler(robot_handler)

        robot_logger.info("App start logging")

        self._robot_logger = robot_logger

    @property
    def normal_logger(self):
        return self._normal_logger

    @property
    def robot_logger(self):
        return self._robot_logger
