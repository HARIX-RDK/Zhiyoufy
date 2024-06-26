import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientConfigItem(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def config_item_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/config-item"

    def config_item_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s config_item_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        config_item_var_path = params.get("config_item_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if config_item_var_path:
            target_config_item = self.get_step_var(config_item_var_path)
        else:
            target_config_item = self.config_item_get_single_by_name(params)

        if not target_config_item:
            self.config_item_create(params)
        elif update_if_exist:
            self.config_item_update(params)

    def config_item_create(self, params):
        log_prefix = "%s %s config_item_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)

            config_collection_var_path = params["config_collection_var_path"]

            target_collection = self.get_step_var(config_collection_var_path)

            req_url = f"{self.config_item_api_prefix}/add-config-item"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "environmentId": target_environment["id"],
                "environmentName": target_environment["name"],
                "collectionId": target_collection["id"],
                "collectionName": target_collection["name"],
            }

            for key in ["name", "configValue", "tags", "sort", "disabled"]:
                if key in params:
                    body_info[key] = params[key]

            if "value_file" in params:
                with open(params["value_file"], "r", encoding="utf-8") as vf:
                    value = vf.read()
                    body_info["configValue"] = value

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_item_del(self, params):
        log_prefix = "%s %s config_item_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            config_item_var_path = params.get("config_item_var_path", None)

            if config_item_var_path:
                target_config_item = self.get_step_var(config_item_var_path)
            else:
                target_config_item = self.config_item_get_single_by_name(params)

            if target_config_item:
                self.config_item_del_by_id(target_config_item["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_item_get_list(self, params, trace_on=True):
        log_prefix = "%s %s config_item_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            config_collection_var_path = params["config_collection_var_path"]

            target_config_collection = self.get_step_var(config_collection_var_path)

            collection_id = target_config_collection["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = f"{self.config_item_api_prefix}/config-item-list/?collectionId={collection_id}&pageNum={page_num}"

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

    def config_item_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s config_item_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            config_collection_var_path = params["config_collection_var_path"]
            name = params["name"]
            config_item_var_path = params.get("config_item_var_path", None)

            config_item_list = self.config_item_get_list({
                "config_collection_var_path": config_collection_var_path,
                "exactMatch": True,
                "keyword": name,
            })

            target_config_item = None

            for config_item in config_item_list:
                if config_item["name"] == name:
                    target_config_item = config_item
                    break

            if config_item_var_path:
                self.set_step_var(config_item_var_path, target_config_item)

            return target_config_item
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_item_update(self, params):
        log_prefix = "%s %s config_item_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            config_item_var_path = params.get("config_item_var_path", None)
            timeout = params.get("timeout", 10)

            if config_item_var_path:
                target_config_item = self.get_step_var(config_item_var_path)
            else:
                target_config_item = self.config_item_get_single_by_name(params)

            if not target_config_item:
                raise Exception("%s not found config_item" % log_prefix)

            req_url = f"{self.config_item_api_prefix}/update-config-item/{target_config_item['id']}"

            body_info = {}

            for key in ["name", "configValue", "tags", "sort", "disabled", "inUse"]:
                if key in params:
                    body_info[key] = params[key]

            if "value_file" in params:
                with open(params["value_file"], "r", encoding="utf-8") as vf:
                    value = vf.read()
                    body_info["configValue"] = value

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def config_item_del_by_id(self, config_item_id):
        log_prefix = "%s %s config_item_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with config_item_id %s" % (
            log_prefix, config_item_id))

        try:
            req_url = f"{self.config_item_api_prefix}/del-config-item/{config_item_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
