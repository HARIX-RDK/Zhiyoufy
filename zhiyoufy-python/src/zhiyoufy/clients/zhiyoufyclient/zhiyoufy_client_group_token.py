import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientGroupToken(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def group_token_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/group-token"

    def group_token_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s group_token_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        group_token_var_path = params.get("group_token_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if group_token_var_path:
            target_group_token = self.get_step_var(group_token_var_path)
        else:
            target_group_token = self.group_token_get_single_by_name(params)

        if not target_group_token:
            self.group_token_create(params)
        elif update_if_exist:
            self.group_token_update(params)

    def group_token_create(self, params):
        log_prefix = "%s %s group_token_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_var_path = params["worker_app_var_path"]

            target_worker_app = self.get_step_var(worker_app_var_path)

            worker_group_var_path = params["worker_group_var_path"]

            target_worker_group = self.get_step_var(worker_group_var_path)

            req_url = f"{self.group_token_api_prefix}/add-group-token"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "workerAppId": target_worker_app["id"],
                "workerAppName": target_worker_app["name"],
                "workerGroupId": target_worker_group["id"],
                "workerGroupName": target_worker_group["name"],
            }

            for key in ["name", "secret", "expiryTime", "description"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def group_token_del(self, params):
        log_prefix = "%s %s group_token_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            group_token_var_path = params.get("group_token_var_path", None)

            if group_token_var_path:
                target_group_token = self.get_step_var(group_token_var_path)
            else:
                target_group_token = self.group_token_get_single_by_name(params)

            if target_group_token:
                self.group_token_del_by_id(target_group_token["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def group_token_get_list(self, params, trace_on=True):
        log_prefix = "%s %s group_token_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            worker_group_var_path = params["worker_group_var_path"]

            target_worker_group = self.get_step_var(worker_group_var_path)

            worker_group_id = target_worker_group["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = f"{self.group_token_api_prefix}/group-token-list/?" \
                      f"workerGroupId={worker_group_id}&pageNum={page_num}"

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

    def group_token_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s group_token_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            worker_group_var_path = params["worker_group_var_path"]
            name = params["name"]
            must_exist = params.get("must_exist", False)
            group_token_var_path = params.get("group_token_var_path", None)

            group_token_list = self.group_token_get_list({
                "worker_group_var_path": worker_group_var_path,
                "exactMatch": True,
                "keyword": name,
            })

            target_group_token = None

            for group_token in group_token_list:
                if group_token["name"] == name:
                    target_group_token = group_token
                    break

            if not target_group_token and must_exist:
                raise Exception(f"Not found group token {name}")

            if group_token_var_path:
                self.set_step_var(group_token_var_path, target_group_token)

            return target_group_token
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def group_token_update(self, params):
        log_prefix = "%s %s group_token_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            group_token_var_path = params.get("group_token_var_path", None)
            timeout = params.get("timeout", 10)

            if group_token_var_path:
                target_group_token = self.get_step_var(group_token_var_path)
            else:
                target_group_token = self.group_token_get_single_by_name(params)

            if not target_group_token:
                raise Exception("%s not found group_token" % log_prefix)

            req_url = f"{self.group_token_api_prefix}/update-group-token/{target_group_token['id']}"

            body_info = {}

            for key in ["name", "secret", "expiryTime", "description"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def group_token_del_by_id(self, group_token_id):
        log_prefix = "%s %s group_token_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with group_token_id %s" % (
            log_prefix, group_token_id))

        try:
            req_url = f"{self.group_token_api_prefix}/del-group-token/{group_token_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
