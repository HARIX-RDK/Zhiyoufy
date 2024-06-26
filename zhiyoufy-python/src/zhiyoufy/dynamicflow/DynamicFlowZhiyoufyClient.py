from zhiyoufy.common.utils import CheckUtil


class DynamicFlowZhiyoufyClient:
    def __init__(self):
        self.log_prefix = "%s: " % type(self).__name__
        self.type_prefix = "zhiyoufy_"

        self.config_inst = None
        self.robot_logger = None
        self.context = None

        self._zhiyoufy_client = None

    @property
    def zhiyoufy_client(self):
        return self.context.get_lib_client("zhiyoufy_client")

    def setup(self, dynamic_flow_base_inst):
        self.config_inst = dynamic_flow_base_inst.config_inst
        self.robot_logger = dynamic_flow_base_inst.robot_logger
        self.context = dynamic_flow_base_inst.context

        self.context.add_lib_client_factory("zhiyoufy_client", self.build_zhiyoufy_client)

    def cleanup(self, case_context):
        pass

    def build_zhiyoufy_client(self):
        if self._zhiyoufy_client is None:
            from zhiyoufy.clients.zhiyoufyclient.zhiyoufy_client import ZhiyoufyClient
            self._zhiyoufy_client = ZhiyoufyClient()
        return self._zhiyoufy_client

    def process(self, data):
        if data['type'].startswith('zhiyoufy_base_'):
            self.process_type_of_zhiyoufy_base(data)
        elif data['type'].startswith('zhiyoufy_config_collection_'):
            self.process_type_of_zhiyoufy_config_collection(data)
        elif data['type'].startswith('zhiyoufy_config_item_'):
            self.process_type_of_zhiyoufy_config_item(data)
        elif data['type'].startswith('zhiyoufy_config_single_'):
            self.process_type_of_zhiyoufy_config_single(data)
        elif data['type'].startswith('zhiyoufy_environment_'):
            self.process_type_of_zhiyoufy_environment(data)
        elif data['type'].startswith('zhiyoufy_job_folder_'):
            self.process_type_of_zhiyoufy_job_folder(data)
        elif data['type'].startswith('zhiyoufy_job_template_'):
            self.process_type_of_zhiyoufy_job_template(data)
        elif data['type'].startswith('zhiyoufy_project_'):
            self.process_type_of_zhiyoufy_project(data)
        elif data['type'].startswith('zhiyoufy_user_'):
            self.process_type_of_zhiyoufy_user(data)
        elif data['type'].startswith('zhiyoufy_util_'):
            self.process_type_of_zhiyoufy_util(data)
        elif data['type'].startswith('zhiyoufy_worker_app_'):
            self.process_type_of_zhiyoufy_worker_app(data)
        elif data['type'].startswith('zhiyoufy_worker_group_'):
            self.process_type_of_zhiyoufy_worker_group(data)
        elif data['type'].startswith('zhiyoufy_group_token_'):
            self.process_type_of_zhiyoufy_group_token(data)
        elif data['type'].startswith('zhiyoufy_job_run_'):
            self.process_type_of_zhiyoufy_job_run(data)
        else:
            raise Exception("Unknown zhiyoufy type %s" % data['type'])

    def process_type_of_zhiyoufy_base(self, data):
        if data['type'] == 'zhiyoufy_base_extract_token':
            self.process_type_zhiyoufy_base_extract_token(data)
        elif data['type'] == 'zhiyoufy_base_login':
            self.process_type_zhiyoufy_base_login(data)
        elif data['type'] == 'zhiyoufy_base_logout':
            self.process_type_zhiyoufy_base_logout(data)
        else:
            raise Exception("Unknown zhiyoufy_base type %s" % data['type'])

    def process_type_of_zhiyoufy_config_collection(self, data):
        if data['type'] == 'zhiyoufy_config_collection_create':
            self.process_type_zhiyoufy_config_collection_create(data)
        elif data['type'] == 'zhiyoufy_config_collection_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_config_collection_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_config_collection_del':
            self.process_type_zhiyoufy_config_collection_del(data)
        elif data['type'] == 'zhiyoufy_config_collection_get_list':
            self.process_type_zhiyoufy_config_collection_get_list(data)
        elif data['type'] == 'zhiyoufy_config_collection_get_single_by_name':
            self.process_type_zhiyoufy_config_collection_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_config_collection type %s" % data['type'])

    def process_type_of_zhiyoufy_config_item(self, data):
        if data['type'] == 'zhiyoufy_config_item_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_config_item_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_config_item_del_by_id':
            self.process_type_zhiyoufy_config_item_del_by_id(data)
        elif data['type'] == 'zhiyoufy_config_item_del_by_name':
            self.process_type_zhiyoufy_config_item_del_by_name(data)
        elif data['type'] == 'zhiyoufy_config_item_get_list':
            self.process_type_zhiyoufy_config_item_get_list(data)
        elif data['type'] == 'zhiyoufy_config_item_get_single_by_name':
            self.process_type_zhiyoufy_config_item_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_config_item type %s" % data['type'])

    def process_type_of_zhiyoufy_config_single(self, data):
        if data['type'] == 'zhiyoufy_config_single_create':
            self.process_type_zhiyoufy_config_single_create(data)
        elif data['type'] == 'zhiyoufy_config_single_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_config_single_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_config_single_del':
            self.process_type_zhiyoufy_config_single_del(data)
        elif data['type'] == 'zhiyoufy_config_single_get_list':
            self.process_type_zhiyoufy_config_single_get_list(data)
        elif data['type'] == 'zhiyoufy_config_single_get_single_by_name':
            self.process_type_zhiyoufy_config_single_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_config_single_update':
            self.process_type_zhiyoufy_config_single_update(data)
        else:
            raise Exception("Unknown zhiyoufy_config_single type %s" % data['type'])

    def process_type_of_zhiyoufy_environment(self, data):
        if data['type'] == 'zhiyoufy_environment_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_environment_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_environment_del_by_id':
            self.process_type_zhiyoufy_environment_del_by_id(data)
        elif data['type'] == 'zhiyoufy_environment_del_by_name':
            self.process_type_zhiyoufy_environment_del_by_name(data)
        elif data['type'] == 'zhiyoufy_environment_get_list':
            self.process_type_zhiyoufy_environment_get_list(data)
        elif data['type'] == 'zhiyoufy_environment_get_single_by_name':
            self.process_type_zhiyoufy_environment_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_environment_user_sync':
            self.process_type_zhiyoufy_environment_user_sync(data)
        else:
            raise Exception("Unknown zhiyoufy_environment type %s" % data['type'])

    def process_type_of_zhiyoufy_job_folder(self, data):
        if data['type'] == 'zhiyoufy_job_folder_create':
            self.process_type_zhiyoufy_job_folder_create(data)
        elif data['type'] == 'zhiyoufy_job_folder_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_job_folder_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_job_folder_del':
            self.process_type_zhiyoufy_job_folder_del(data)
        elif data['type'] == 'zhiyoufy_job_folder_get_list':
            self.process_type_zhiyoufy_job_folder_get_list(data)
        elif data['type'] == 'zhiyoufy_job_folder_get_single_by_name':
            self.process_type_zhiyoufy_job_folder_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_job_folder type %s" % data['type'])

    def process_type_of_zhiyoufy_job_template(self, data):
        if data['type'] == 'zhiyoufy_job_template_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_job_template_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_job_template_multi_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_job_template_multi_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_job_template_del':
            self.process_type_zhiyoufy_job_template_del(data)
        elif data['type'] == 'zhiyoufy_job_template_get_list':
            self.process_type_zhiyoufy_job_template_get_list(data)
        elif data['type'] == 'zhiyoufy_job_template_get_single_by_name':
            self.process_type_zhiyoufy_job_template_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_job_template type %s" % data['type'])

    def process_type_of_zhiyoufy_project(self, data):
        if data['type'] == 'zhiyoufy_project_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_project_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_project_del':
            self.process_type_zhiyoufy_project_del(data)
        elif data['type'] == 'zhiyoufy_project_get_list':
            self.process_type_zhiyoufy_project_get_list(data)
        elif data['type'] == 'zhiyoufy_project_get_single_by_name':
            self.process_type_zhiyoufy_project_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_project_user_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_project_user_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_project_user_del':
            self.process_type_zhiyoufy_project_user_del(data)
        elif data['type'] == 'zhiyoufy_project_user_get_list':
            self.process_type_zhiyoufy_project_user_get_list(data)
        elif data['type'] == 'zhiyoufy_project_user_get_single_by_name':
            self.process_type_zhiyoufy_project_user_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_project_user_sync':
            self.process_type_zhiyoufy_project_user_sync(data)
        else:
            raise Exception("Unknown zhiyoufy_project type %s" % data['type'])

    def process_type_of_zhiyoufy_user(self, data):
        if data['type'] == 'zhiyoufy_user_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_user_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_user_del_by_id':
            self.process_type_zhiyoufy_user_del_by_id(data)
        elif data['type'] == 'zhiyoufy_user_del_by_name':
            self.process_type_zhiyoufy_user_del_by_name(data)
        elif data['type'] == 'zhiyoufy_user_get_list':
            self.process_type_zhiyoufy_user_get_list(data)
        elif data['type'] == 'zhiyoufy_user_get_single_by_name':
            self.process_type_zhiyoufy_user_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_user_update':
            self.process_type_zhiyoufy_user_update(data)
        elif data['type'] == 'zhiyoufy_user_get_role_list':
            self.process_type_zhiyoufy_user_get_role_list(data)
        elif data['type'] == 'zhiyoufy_user_sync_user_role_list':
            self.process_type_zhiyoufy_user_sync_user_role_list(data)
        else:
            raise Exception("Unknown zhiyoufy_user type %s" % data['type'])

    def process_type_of_zhiyoufy_util(self, data):
        if data['type'] == 'zhiyoufy_util_create_config_items_in_batch':
            self.process_type_zhiyoufy_util_create_config_items_in_batch(data)
        else:
            raise Exception("Unknown zhiyoufy_util type %s" % data['type'])

    def process_type_of_zhiyoufy_worker_app(self, data):
        if data['type'] == 'zhiyoufy_worker_app_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_worker_app_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_worker_app_del':
            self.process_type_zhiyoufy_worker_app_del(data)
        elif data['type'] == 'zhiyoufy_worker_app_get_list':
            self.process_type_zhiyoufy_worker_app_get_list(data)
        elif data['type'] == 'zhiyoufy_worker_app_get_single_by_name':
            self.process_type_zhiyoufy_worker_app_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_worker_app_user_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_worker_app_user_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_worker_app_user_del':
            self.process_type_zhiyoufy_worker_app_user_del(data)
        elif data['type'] == 'zhiyoufy_worker_app_user_get_list':
            self.process_type_zhiyoufy_worker_app_user_get_list(data)
        elif data['type'] == 'zhiyoufy_worker_app_user_get_single_by_name':
            self.process_type_zhiyoufy_worker_app_user_get_single_by_name(data)
        elif data['type'] == 'zhiyoufy_worker_app_user_sync':
            self.process_type_zhiyoufy_worker_app_user_sync(data)
        else:
            raise Exception("Unknown zhiyoufy_worker_app type %s" % data['type'])

    def process_type_of_zhiyoufy_worker_group(self, data):
        if data['type'] == 'zhiyoufy_worker_group_create':
            self.process_type_zhiyoufy_worker_group_create(data)
        elif data['type'] == 'zhiyoufy_worker_group_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_worker_group_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_worker_group_del':
            self.process_type_zhiyoufy_worker_group_del(data)
        elif data['type'] == 'zhiyoufy_worker_group_get_list':
            self.process_type_zhiyoufy_worker_group_get_list(data)
        elif data['type'] == 'zhiyoufy_worker_group_get_single_by_name':
            self.process_type_zhiyoufy_worker_group_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_worker_group type %s" % data['type'])

    def process_type_of_zhiyoufy_group_token(self, data):
        if data['type'] == 'zhiyoufy_group_token_create_if_missing_update_if_requested':
            self.process_type_zhiyoufy_group_token_create_if_missing_update_if_requested(data)
        elif data['type'] == 'zhiyoufy_group_token_del':
            self.process_type_zhiyoufy_group_token_del(data)
        elif data['type'] == 'zhiyoufy_group_token_get_list':
            self.process_type_zhiyoufy_group_token_get_list(data)
        elif data['type'] == 'zhiyoufy_group_token_get_single_by_name':
            self.process_type_zhiyoufy_group_token_get_single_by_name(data)
        else:
            raise Exception("Unknown zhiyoufy_group_token type %s" % data['type'])

    def process_type_of_zhiyoufy_job_run(self, data):
        if data['type'] == 'zhiyoufy_job_run_create':
            self.process_type_zhiyoufy_job_run_create(data)
        elif data['type'] == 'zhiyoufy_job_run_del':
            self.process_type_zhiyoufy_job_run_del(data)
        elif data['type'] == 'zhiyoufy_job_run_get_single':
            self.process_type_zhiyoufy_job_run_get_single(data)
        elif data['type'] == 'zhiyoufy_job_run_get_child_result_list':
            self.process_type_zhiyoufy_job_run_get_child_result_list(data)
        elif data['type'] == 'zhiyoufy_job_run_get_full_child_result_list':
            self.process_type_zhiyoufy_job_run_get_full_child_result_list(data)
        elif data['type'] == 'zhiyoufy_job_run_wait_job_done':
            self.process_type_zhiyoufy_job_run_wait_job_done(data)
        else:
            raise Exception("Unknown zhiyoufy_job_run type %s" % data['type'])

    # region TypeZhiyoufyBase
    def process_type_zhiyoufy_base_extract_token(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.extract_token(data)

    def process_type_zhiyoufy_base_login(self, data):
        zhiyoufy_client = self.zhiyoufy_client
        params = self.context.params

        zhiyoufy_client.login(params["zhiyoufy_addr"], data["username"], data["password"])

    def process_type_zhiyoufy_base_logout(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.logout()

    # endregion

    # region TypeZhiyoufyConfigCollection
    def process_type_zhiyoufy_config_collection_create(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_collection_client.config_collection_create(data)

    def process_type_zhiyoufy_config_collection_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_collection_client.config_collection_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_config_collection_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_collection_client.config_collection_del(data)

    def process_type_zhiyoufy_config_collection_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_collection_client.config_collection_get_list(data)

    def process_type_zhiyoufy_config_collection_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_collection_client.config_collection_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyConfigItem
    def process_type_zhiyoufy_config_item_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        target_config_item = zhiyoufy_client.config_item_client.config_item_get_single_by_name(data)

        if not target_config_item:
            zhiyoufy_client.config_item_client.config_item_create(data)
        else:
            zhiyoufy_client.config_item_client.config_item_update(data)

    def process_type_zhiyoufy_config_item_del_by_id(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_item_client.config_item_del_by_id(data["id"])

    def process_type_zhiyoufy_config_item_del_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_item_client.config_item_del_by_name(data)

    def process_type_zhiyoufy_config_item_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_item_client.config_item_get_list(data)

    def process_type_zhiyoufy_config_item_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.config_item_client.config_item_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyConfigSingle
    def process_type_zhiyoufy_config_single_create(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_single_client.config_single_create(data)

    def process_type_zhiyoufy_config_single_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_single_client.config_single_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_config_single_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_single_client.config_single_del(data)

    def process_type_zhiyoufy_config_single_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_single_client.config_single_get_list(data)

    def process_type_zhiyoufy_config_single_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        target_config_single = zhiyoufy_client.config_single_client.config_single_get_single_by_name(data)

        if "expected_rsp" in data:
            is_expected = CheckUtil.check_dict_expected(
                data["expected_rsp"], data["expected_rsp"], target_config_single)

            if not is_expected:
                self.robot_logger.error(f"{self.log_prefix}: Not match the expected rsp! \n"
                                        f" Expected {data['expected_rsp']}\n Real {target_config_single}")
                raise Exception(f"zhiyoufy_config_single_get_single_by_name: Not match the expected rsp!")

    def process_type_zhiyoufy_config_single_update(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.config_single_client.config_single_update(data)

    # endregion

    # region TypeZhiyoufyEnvironment
    def process_type_zhiyoufy_environment_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.environment_client.environment_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_environment_del_by_id(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.environment_client.environment_del_by_id(data["id"])

    def process_type_zhiyoufy_environment_del_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.environment_client.environment_del_by_name(data)

    def process_type_zhiyoufy_environment_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.environment_client.environment_get_list(data)

    def process_type_zhiyoufy_environment_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.environment_client.environment_get_single_by_name(data)

    def process_type_zhiyoufy_environment_user_sync(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.environment_client.environment_user_sync(data)

    # endregion

    # region TypeZhiyoufyJobFolder
    def process_type_zhiyoufy_job_folder_create(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.job_folder_client.job_folder_create(data)

    def process_type_zhiyoufy_job_folder_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.job_folder_client.job_folder_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_job_folder_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_folder_client.job_folder_del(data)

    def process_type_zhiyoufy_job_folder_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_folder_client.job_folder_get_list(data)

    def process_type_zhiyoufy_job_folder_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_folder_client.job_folder_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyJobTemplate
    def process_type_zhiyoufy_job_template_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.job_template_client.job_template_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_job_template_multi_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client
        zhiyoufy_client.job_template_client.job_template_multi_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_job_template_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_template_client.job_template_del(data)

    def process_type_zhiyoufy_job_template_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_template_client.job_template_get_list(data)

    def process_type_zhiyoufy_job_template_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.job_template_client.job_template_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyProject
    def process_type_zhiyoufy_project_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.project_client.project_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_project_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_del(data)

    def process_type_zhiyoufy_project_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_get_list(data)

    def process_type_zhiyoufy_project_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_get_single_by_name(data)

    def process_type_zhiyoufy_project_user_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.project_client.project_user_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_project_user_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_user_del(data)

    def process_type_zhiyoufy_project_user_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_user_get_list(data)

    def process_type_zhiyoufy_project_user_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.project_client.project_user_get_single_by_name(data)

    def process_type_zhiyoufy_project_user_sync(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.project_client.project_user_sync(data)

    # endregion

    # region TypeZhiyoufyWorkerApp
    def process_type_zhiyoufy_worker_app_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.worker_app_client.worker_app_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_worker_app_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_del(data)

    def process_type_zhiyoufy_worker_app_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_get_list(data)

    def process_type_zhiyoufy_worker_app_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_get_single_by_name(data)

    def process_type_zhiyoufy_worker_app_user_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.worker_app_client.worker_app_user_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_worker_app_user_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_user_del(data)

    def process_type_zhiyoufy_worker_app_user_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_user_get_list(data)

    def process_type_zhiyoufy_worker_app_user_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_app_client.worker_app_user_get_single_by_name(data)

    def process_type_zhiyoufy_worker_app_user_sync(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.worker_app_client.worker_app_user_sync(data)

    # endregion

    # region TypeZhiyoufyWorkerGroup
    def process_type_zhiyoufy_worker_group_create(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.worker_group_client.worker_group_create(data)

    def process_type_zhiyoufy_worker_group_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.worker_group_client.worker_group_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_worker_group_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_group_client.worker_group_del(data)

    def process_type_zhiyoufy_worker_group_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_group_client.worker_group_get_list(data)

    def process_type_zhiyoufy_worker_group_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.worker_group_client.worker_group_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyGroupToken
    def process_type_zhiyoufy_group_token_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        if "token" in data:
            data["name"] = data["token"]["name"]
            data["description"] = data["token"]["description"]
            data["secret"] = data["token"]["secret"]
            data["description"] = data["token"]["description"]

        if "worker_group_var_path" not in data:
            worker_group = zhiyoufy_client.worker_group_client.worker_group_get_single_by_name(
                {"worker_app_var_path": data["worker_app_var_path"],
                 "name": data["worker_group_name"]})
            if not worker_group:
                raise Exception("worker_group not exist, so we cannot create token for it!!!")
            worker_group_var = "worker_group_var"
            data["worker_group_var_path"] = worker_group_var
            self.context.set_step_var(worker_group_var, worker_group)

        zhiyoufy_client.group_token_client.group_token_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_group_token_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.group_token_client.group_token_del(data)

    def process_type_zhiyoufy_group_token_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.group_token_client.group_token_get_list(data)

    def process_type_zhiyoufy_group_token_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.group_token_client.group_token_get_single_by_name(data)

    # endregion

    # region TypeZhiyoufyUser
    def process_type_zhiyoufy_user_create_if_missing_update_if_requested(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.user_client.user_create_if_missing_update_if_requested(data)

    def process_type_zhiyoufy_user_del_by_id(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.user_del_by_id(data["id"])

    def process_type_zhiyoufy_user_del_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.user_del_by_name(data)

    def process_type_zhiyoufy_user_get_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.user_get_list(data)

    def process_type_zhiyoufy_user_get_single_by_name(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.user_get_single_by_name(data)

    def process_type_zhiyoufy_user_update(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.user_client.user_update(data)

    def process_type_zhiyoufy_user_get_role_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.role_get_list(data)

    def process_type_zhiyoufy_user_sync_user_role_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        rsp = zhiyoufy_client.user_client.user_role_list_sync(data)

    # endregion

    # region TypeZhiyoufyUtil
    def process_type_zhiyoufy_util_create_config_items_in_batch(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.util_client.util_create_config_items_in_batch(data)
    # endregion

    # region TypeZhiyoufyJobRun
    def process_type_zhiyoufy_job_run_create(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_create(data)

    def process_type_zhiyoufy_job_run_del(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_del(data)

    def process_type_zhiyoufy_job_run_get_single(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_get_single(data)

    def process_type_zhiyoufy_job_run_get_child_result_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_get_child_result_list(data)

    def process_type_zhiyoufy_job_run_get_full_child_result_list(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_get_full_child_result_list(data)

    def process_type_zhiyoufy_job_run_wait_job_done(self, data):
        zhiyoufy_client = self.zhiyoufy_client

        zhiyoufy_client.run_client.job_run_wait_job_done(data)

    # endregion

