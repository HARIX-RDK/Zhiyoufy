from .. import zhiyoufy_context

from . import GlobalLibraryBase


class CustomLibraryBase:
    def __init__(self):
        self.log_prefix = "%s:" % type(self).__name__

        self.global_library_base = GlobalLibraryBase.get_global_library_base()
        self.config_inst = self.global_library_base.config_inst
        self.global_context = zhiyoufy_context.get_global_context()
        self.local_context = {}
        self.robot_logger = self.global_library_base.robot_logger

        # General
        self.headers = {
            'Content-Type': "application/json;charset=UTF-8",
            'Accept': "application/json",
            'Accept-Encoding': 'gzip, deflate, br'
        }

        self.headers_without_content_type = {
            'Accept': "application/json",
            'Accept-Encoding': 'gzip, deflate, br'
        }

    def get_step_var(self, step_var_path, raise_exception_if_miss=None):
        if raise_exception_if_miss is None:
            return self.global_library_base.get_step_var(step_var_path)
        else:
            return self.global_library_base.get_step_var(step_var_path, raise_exception_if_miss)

    def set_step_var(self, step_var_path, step_var_value):
        self.global_library_base.set_step_var(step_var_path, step_var_value)
