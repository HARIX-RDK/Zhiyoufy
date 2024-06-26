class CustomLibraryBridgeBase:
    def __init__(self, parent):
        self.log_prefix = "%s:" % type(self).__name__

        self.parent = parent

    @property
    def config_inst(self):
        return self.parent.config_inst

    # 不同library共享数据
    @property
    def global_context(self):
        return self.parent.global_context

    # 单个library内部共享数据
    @property
    def local_context(self):
        return self.parent.local_context

    @property
    def headers(self):
        return self.parent.headers

    @property
    def headers_without_content_type(self):
        return self.parent.headers_without_content_type

    @property
    def norm_timeout(self):
        return self.config_inst.norm_timeout

    @property
    def robot_logger(self):
        return self.parent.robot_logger

    def check_login(self):
        return self.parent.check_login()

    # pass info between steps, similar to programming variables
    def get_step_var(self, step_var_path, raise_exception_if_miss=None):
        return self.parent.get_step_var(step_var_path, raise_exception_if_miss)

    def set_step_var(self, step_var_path, step_var_value):
        self.parent.set_step_var(step_var_path, step_var_value)
