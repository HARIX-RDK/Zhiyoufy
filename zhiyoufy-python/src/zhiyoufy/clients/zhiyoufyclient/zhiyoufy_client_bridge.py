from zhiyoufy.common.dynamicflow.CustomLibraryBridgeBase import CustomLibraryBridgeBase


class ZhiyoufyClientBridge(CustomLibraryBridgeBase):
    def __init__(self, parent_client):
        super().__init__(parent_client)
        self.parent_client = parent_client

    @property
    def api_base_url(self):
        return self.parent_client.api_base_url

    @property
    def zhiyoufy_addr(self):
        return self.parent_client.zhiyoufy_addr

    @property
    def zhiyoufy_addr_base(self):
        return self.parent_client.zhiyoufy_addr_base

    @property
    def user_client(self):
        return self.parent_client.user_client

    @property
    def environment_client(self):
        return self.parent_client.environment_client

    @property
    def config_single_client(self):
        return self.parent_client.config_single_client

    @property
    def config_collection_client(self):
        return self.parent_client.config_collection_client

    @property
    def config_item_client(self):
        return self.parent_client.config_item_client

    @property
    def job_folder_client(self):
        return self.parent_client.job_folder_client

    @property
    def job_template_client(self):
        return self.parent_client.job_template_client

    @property
    def project_client(self):
        return self.parent_client.project_client

    @property
    def group_token_client(self):
        return self.parent_client.group_token_client

    @property
    def worker_app_client(self):
        return self.parent_client.worker_app_client

    @property
    def worker_group_client(self):
        return self.parent_client.worker_group_client

    @property
    def run_client(self):
        return self.parent_client.run_client

    def clear_user_cache(self, user_name=None):
        return self.parent_client.clear_user_cache(user_name)

    def set_user_cache(self, in_user):
        return self.parent_client.set_user_cache(in_user)

    def get_user_cache(self, user_name):
        return self.parent_client.get_user_cache(user_name)

    def set_role_cache(self, in_role):
        return self.parent_client.set_role_cache(in_role)

    def get_role_cache(self, role_name):
        return self.parent_client.get_role_cache(role_name)

    def clear_environment_cache(self, environment_name=None):
        return self.parent_client.clear_environment_cache(environment_name)

    def set_environment_cache(self, in_environment):
        return self.parent_client.set_environment_cache(in_environment)

    def get_environment_cache(self, environment_name):
        return self.parent_client.get_environment_cache(environment_name)

    def clear_config_cache(self, config_name=None):
        return self.parent_client.clear_config_cache(config_name)

    def set_config_cache(self, in_config):
        return self.parent_client.set_config_cache(in_config)

    def get_config_cache(self, config_name):
        return self.parent_client.get_config_cache(config_name)

    def check_login(self):
        self.parent_client.check_login()
