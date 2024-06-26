import json
import requests
import os

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientConfigSingle(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def config_single_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/config-single"

    def config_single_create(self, params):
        log_prefix = f"{self.log_prefix} {self.zhiyoufy_addr_base} config_single_create:"

        self.robot_logger.info(f"{log_prefix} Enter with \n{StrUtil.pprint(params)}")

        try:
            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)

            req_url = f"{self.config_single_api_prefix}/add-config-single"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "environmentId": target_environment["id"],
                "environmentName": target_environment["name"],
            }

            for key in ["name", "configValue"]:
                if key in params:
                    body_info[key] = params[key]

            if "value_file" in params:
                value_file_path = params["value_file"]
                if not os.path.isabs(value_file_path):
                    value_file_path = os.path.join(self.config_inst.root_dir, value_file_path)
                with open(value_file_path, "r", encoding="utf-8") as vf:
                    value = vf.read()
                    body_info["configValue"] = value

            self.robot_logger.info(f"{log_prefix} req_url {req_url}")
            self.robot_logger.info(f"{log_prefix} req_body {StrUtil.pprint(body_info)}")

            r = requests.post(req_url, headers=self.headers, json=body_info,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_single_create_if_missing_update_if_requested(self, params):
        log_prefix = (f"{self.log_prefix} {self.zhiyoufy_addr_base} "
                      f"config_single_create_if_missing_update_if_requested:")

        self.robot_logger.info(f"{log_prefix} Enter with \n{StrUtil.pprint(params)}")

        config_single_var_path = params.get("config_single_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if config_single_var_path:
            target_config_single = self.get_step_var(config_single_var_path)
        else:
            target_config_single = self.config_single_get_single_by_name(params)

        if not target_config_single:
            self.config_single_create(params)
        elif update_if_exist:
            self.config_single_update(params)

    def config_single_del(self, params):
        log_prefix = f"{self.log_prefix} {self.zhiyoufy_addr_base} config_single_del:"

        self.robot_logger.info(f"{log_prefix} Enter with \n{StrUtil.pprint(params)}")

        try:
            config_single_var_path = params.get("config_single_var_path", None)

            if config_single_var_path:
                target_config_single = self.get_step_var(config_single_var_path)
            else:
                target_config_single = self.config_single_get_single_by_name(params)

            if not target_config_single:
                self.robot_logger.info(f"{log_prefix} not found")
                return

            config_single_id = target_config_single["id"]

            req_url = f"{self.config_single_api_prefix}/del-config-single/{config_single_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_single_get_list(self, params, trace_on=True):
        log_prefix = f"{self.log_prefix} {self.zhiyoufy_addr_base} config_single_get_list:"

        self.robot_logger.info(f"{log_prefix} Enter with \n{StrUtil.pprint(params)}")

        try:
            self.check_login()

            config_single_list_var_path = params.get("config_single_list_var_path", None)

            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)

            env_id = target_environment["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = (f"{self.config_single_api_prefix}/config-single-list/?"
                       f"envId={env_id}&pageNum={page_num}")

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            if exact_match:
                req_url += f"&exactMatch=true"

            self.robot_logger.info(f"{log_prefix} req_url {req_url}")

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            config_single_list = call_rsp["data"]["list"]

            self.robot_logger.info(f"{log_prefix} config_single_list \n"
                                   f"{StrUtil.pprint(config_single_list)}")

            if config_single_list_var_path:
                self.set_step_var(config_single_list_var_path, config_single_list)

            return config_single_list
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_single_get_single_by_name(self, params, trace_on=True):
        log_prefix = (f"{self.log_prefix} {self.zhiyoufy_addr_base} "
                      f"config_single_get_single_by_name:")

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            config_single_list_var_path = params.get("config_single_list_var_path", None)
            config_single_var_path = params.get("config_single_var_path", None)
            must_exist = params.get("must_exist", False)

            environment_var_path = params["environment_var_path"]
            name = params["name"]

            if config_single_list_var_path is not None:
                config_single_list = self.get_step_var(config_single_list_var_path)
            else:
                config_single_list = self.config_single_get_list({
                    "environment_var_path": environment_var_path,
                    "exactMatch": True,
                    "keyword": name,
                })

            target_config_single = None

            for config_single in config_single_list:
                if config_single["name"] == name:
                    target_config_single = config_single
                    break

            if not target_config_single and must_exist:
                raise Exception(f"Not found config single {name}")

            if config_single_var_path:
                self.set_step_var(config_single_var_path, target_config_single)

            return target_config_single
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_single_update(self, params):
        log_prefix = f"{self.log_prefix} {self.zhiyoufy_addr_base} config_single_update:"

        self.robot_logger.info(f"{log_prefix} Enter with \n{StrUtil.pprint(params)}")

        try:
            config_single_var_path = params.get("config_single_var_path", None)
            timeout = params.get("timeout", 10)

            if config_single_var_path:
                target_config_single = self.get_step_var(config_single_var_path)
            else:
                target_config_single = self.config_single_get_single_by_name(params)

            if not target_config_single:
                raise Exception("%s not found config_single" % log_prefix)

            req_url = f"{self.config_single_api_prefix}/update-config-single/{target_config_single['id']}"

            body_info = {}

            for key in ["name", "configValue"]:
                if key in params:
                    body_info[key] = params[key]

            if "value_file" in params:
                value_file_path = params["value_file"]
                if not os.path.isabs(value_file_path):
                    value_file_path = os.path.join(self.config_inst.root_dir, value_file_path)
                with open(value_file_path, "r", encoding="utf-8") as vf:
                    value = vf.read()
                    body_info["configValue"] = value

            data = json.dumps(body_info)

            self.robot_logger.info(f"{log_prefix} req_url {req_url}")

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
