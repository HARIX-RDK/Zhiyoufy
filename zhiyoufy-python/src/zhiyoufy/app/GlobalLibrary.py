import zhiyoufy.common.config as _config

from zhiyoufy.common.dynamicflow.GlobalLibraryBase import GlobalLibraryBase

g_global_library = None


def get_global_library():
    global g_global_library
    return g_global_library


class GlobalLibrary(GlobalLibraryBase):
    ROBOT_LIBRARY_SCOPE = 'GLOBAL'
    ROBOT_LIBRARY_VERSION = "0.1"

    def __init__(self, config_file):
        super().__init__(config_file)

        global g_global_library

        if g_global_library is not None:
            raise Exception("g_global_library is not None, GlobalLibraryBase should only be created once")

        g_global_library = self

    def init_default_config(self):
        """配置config缺省值

        多数配置应该都有缺省值，以减少用户不必要的配置负担
        """
        self.init_test_step_dynamic()
        self.init_test_step_ignore()
        self.init_test_case_ignore()

    def init_test_step_dynamic(self):
        test_step_dynamic = self.config_inst.test_step_dynamic

        if "case_xyz_default_timeout_seconds" not in test_step_dynamic:
            test_step_dynamic["case_xyz_default_timeout_seconds"] = 20

    def init_test_step_ignore(self):
        test_step_ignore = self.config_inst.test_step_ignore

        if "case_abc_step_123" not in test_step_ignore:
            test_step_ignore["case_abc_step_123"] = False

    def init_test_case_ignore(self):
        test_case_ignore = self.config_inst.test_case_ignore
