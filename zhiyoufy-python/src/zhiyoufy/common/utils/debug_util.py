class DebugUtil:
    @staticmethod
    def reset_app():
        from zhiyoufy.common.dynamicflow import GlobalLibraryBase
        from zhiyoufy.common import init_app_base

        GlobalLibraryBase.g_global_library_base = None
        init_app_base._init_app_base_done = False
        init_app_base._logging_setup_done = False
