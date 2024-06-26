import json

import requests
import urllib3
from zhiyoufy.clients.zhiyoufyclient.zhiyoufy_client_job_run import ZhiyoufyClientJobRun
from zhiyoufy.common.dynamicflow.CustomLibraryBase import CustomLibraryBase
from zhiyoufy.common.utils import CheckUtil, LogUtil

from .zhiyoufy_client_config_collection import ZhiyoufyClientConfigCollection
from .zhiyoufy_client_config_item import ZhiyoufyClientConfigItem
from .zhiyoufy_client_config_single import ZhiyoufyClientConfigSingle
from .zhiyoufy_client_environment import ZhiyoufyClientEnvironment
from .zhiyoufy_client_group_token import ZhiyoufyClientGroupToken
from .zhiyoufy_client_job_folder import ZhiyoufyClientJobFolder
from .zhiyoufy_client_job_template import ZhiyoufyClientJobTemplate
from .zhiyoufy_client_project import ZhiyoufyClientProject
from .zhiyoufy_client_user import ZhiyoufyClientUser
from .zhiyoufy_client_util import ZhiyoufyClientUtil
from .zhiyoufy_client_worker_app import ZhiyoufyClientWorkerApp
from .zhiyoufy_client_worker_group import ZhiyoufyClientWorkerGroup

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


class ZhiyoufyClient(CustomLibraryBase):
    def __init__(self):
        super().__init__()

        self.user_client = ZhiyoufyClientUser(parent_client=self)
        self.environment_client = ZhiyoufyClientEnvironment(parent_client=self)
        self.config_single_client = ZhiyoufyClientConfigSingle(parent_client=self)
        self.config_collection_client = ZhiyoufyClientConfigCollection(parent_client=self)
        self.config_item_client = ZhiyoufyClientConfigItem(parent_client=self)
        self.job_folder_client = ZhiyoufyClientJobFolder(parent_client=self)
        self.job_template_client = ZhiyoufyClientJobTemplate(parent_client=self)
        self.project_client = ZhiyoufyClientProject(parent_client=self)
        self.util_client = ZhiyoufyClientUtil(parent_client=self)
        self.group_token_client = ZhiyoufyClientGroupToken(parent_client=self)
        self.worker_app_client = ZhiyoufyClientWorkerApp(parent_client=self)
        self.worker_group_client = ZhiyoufyClientWorkerGroup(parent_client=self)
        self.run_client = ZhiyoufyClientJobRun(parent_client=self)

        self.api_base_url = '/zhiyoufy-api/v1'

        # Login related
        self.zhiyoufy_addr = None
        self.zhiyoufy_addr_base = None
        self.username = None

        self.login_rsp = None
        self.access_token = None

        self.users_cache = {}
        self.roles_cache = {}
        self.environments_cache = {}
        self.configs_cache = {}

    def clear_user_cache(self, user_name=None):
        if user_name is None:
            self.users_cache.clear()
        elif user_name in self.users_cache:
            del self.users_cache[user_name]

    def set_user_cache(self, in_user):
        self.users_cache[in_user["username"]] = in_user

    def get_user_cache(self, user_name):
        return self.users_cache.get(user_name, None)

    def set_role_cache(self, in_role):
        self.roles_cache[in_role["name"]] = in_role

    def get_role_cache(self, role_name):
        return self.roles_cache.get(role_name, None)

    def clear_environment_cache(self, environment_name):
        if environment_name is None:
            self.environments_cache.clear()
        elif environment_name in self.environments_cache:
            del self.environments_cache[environment_name]

    def set_environment_cache(self, in_environment):
        self.environments_cache[in_environment["name"]] = in_environment

    def get_environment_cache(self, environment_name):
        return self.environments_cache.get(environment_name, None)

    def clear_config_cache(self, config_name):
        if config_name is None:
            self.configs_cache.clear()
        elif config_name in self.configs_cache:
            del self.configs_cache[config_name]

    def set_config_cache(self, in_config):
        self.configs_cache[in_config["name"]] = in_config

    def get_config_cache(self, config_name):
        return self.configs_cache.get(config_name, None)

    def login(self, zhiyoufy_addr, username, password):
        self.zhiyoufy_addr = zhiyoufy_addr
        self.username = username

        base_idx = zhiyoufy_addr.find("://")
        if base_idx > -1:
            self.zhiyoufy_addr_base = zhiyoufy_addr[base_idx+3:]
        else:
            self.zhiyoufy_addr_base = zhiyoufy_addr

        log_prefix = "%s %s login:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter to login as %s" % (log_prefix, self.username))

        req_url = "%s%s/user/form-login" % (self.zhiyoufy_addr, self.api_base_url)
        req_body = {
            "username": self.username,
            "password": password,
        }
        req_body = json.dumps(req_body)

        try:
            r = requests.post(req_url, headers=self.headers,
                              data=req_body, timeout=8, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r)

            self.login_rsp = call_rsp
            self.access_token = call_rsp['data']['token']
            self.headers['Authorization'] = 'Bearer %s' % self.access_token
            self.headers_without_content_type['Authorization'] = self.headers['Authorization']
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def logout(self):
        log_prefix = "%s %s logout:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter" % (log_prefix,))

        if self.access_token is None:
            return

        req_url = "%s%s/user/logout" % (self.zhiyoufy_addr, self.api_base_url)

        try:
            r = requests.post(req_url, headers=self.headers,
                              timeout=8, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)

            self.login_rsp = None
            self.access_token = None
            self.headers.pop('Authorization')
            self.headers_without_content_type.pop('Authorization')
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def check_login(self):
        if self.access_token is None:
            raise Exception("Not login yet??")

    def extract_token(self, params):
        self.check_login()

        token_var_path = params["token_var_path"]

        self.set_step_var(token_var_path, self.access_token)

