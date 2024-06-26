import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientConfigCollection(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def config_collection_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/config-collection"

    def config_collection_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s config_collection_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        config_collection_var_path = params.get("config_collection_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if config_collection_var_path:
            target_config_collection = self.get_step_var(config_collection_var_path)
        else:
            target_config_collection = self.config_collection_get_single_by_name(params)

        if not target_config_collection:
            self.config_collection_create(params)
        elif update_if_exist:
            self.config_collection_update(params)

    def config_collection_create(self, params):
        log_prefix = "%s %s config_collection_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)

            req_url = f"{self.config_collection_api_prefix}/add-config-collection"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "environmentId": target_environment["id"],
                "environmentName": target_environment["name"],
            }

            for key in ["name"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_collection_del(self, params):
        log_prefix = "%s %s config_collection_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            config_collection_var_path = params.get("config_collection_var_path", None)

            if config_collection_var_path:
                target_config_collection = self.get_step_var(config_collection_var_path)
            else:
                target_config_collection = self.config_collection_get_single_by_name(params)

            if target_config_collection:
                self.config_collection_del_by_id(target_config_collection["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_collection_get_list(self, params, trace_on=True):
        log_prefix = "%s %s config_collection_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)

            env_id = target_environment["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = f"{self.config_collection_api_prefix}/config-collection-list/?envId={env_id}&pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            if exact_match:
                req_url += f"&exactMatch=true"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_collection_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s config_collection_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            environment_var_path = params["environment_var_path"]
            name = params["name"]
            config_collection_var_path = params.get("config_collection_var_path", None)

            config_collection_list = self.config_collection_get_list({
                "environment_var_path": environment_var_path,
                "exactMatch": True,
                "keyword": name,
            })

            target_config_collection = None

            for config_collection in config_collection_list:
                if config_collection["name"] == name:
                    target_config_collection = config_collection
                    break

            if config_collection_var_path:
                self.set_step_var(config_collection_var_path, target_config_collection)

            return target_config_collection
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_collection_update(self, params):
        log_prefix = "%s %s config_collection_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            config_collection_var_path = params.get("config_collection_var_path", None)
            timeout = params.get("timeout", 10)

            if config_collection_var_path:
                target_config_collection = self.get_step_var(config_collection_var_path)
            else:
                target_config_collection = self.config_collection_get_single_by_name(params)

            if not target_config_collection:
                raise Exception("%s not found config_collection" % log_prefix)

            req_url = f"{self.config_collection_api_prefix}/update-config-collection/{target_config_collection['id']}"

            body_info = {}

            for key in ["name"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_collection_del_by_id(self, config_collection_id):
        log_prefix = "%s %s config_collection_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with config_collection_id %s" % (
            log_prefix, config_collection_id))

        try:
            req_url = f"{self.config_collection_api_prefix}/del-config-collection/{config_collection_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
