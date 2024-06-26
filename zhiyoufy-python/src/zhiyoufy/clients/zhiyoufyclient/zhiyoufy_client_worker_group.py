import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientWorkerGroup(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def worker_group_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/worker-group"

    def worker_group_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s worker_group_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        worker_group_var_path = params.get("worker_group_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if worker_group_var_path:
            target_worker_group = self.get_step_var(worker_group_var_path)
        else:
            target_worker_group = self.worker_group_get_single_by_name(params)

        if not target_worker_group:
            self.worker_group_create(params)
        elif update_if_exist:
            self.worker_group_update(params)

    def worker_group_create(self, params):
        log_prefix = "%s %s worker_group_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_var_path = params["worker_app_var_path"]

            target_worker_app = self.get_step_var(worker_app_var_path)

            req_url = f"{self.worker_group_api_prefix}/add-worker-group"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "workerAppId": target_worker_app["id"],
                "workerAppName": target_worker_app["name"],
            }

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            for key in ["name", "description"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_group_del(self, params):
        log_prefix = "%s %s worker_group_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_group_var_path = params.get("worker_group_var_path", None)

            if worker_group_var_path:
                target_worker_group = self.get_step_var(worker_group_var_path)
            else:
                target_worker_group = self.worker_group_get_single_by_name(params)

            if target_worker_group:
                self.worker_group_del_by_id(target_worker_group["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_group_get_list(self, params, trace_on=True):
        log_prefix = "%s %s worker_group_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            worker_app_var_path = params["worker_app_var_path"]

            target_worker_app = self.get_step_var(worker_app_var_path)

            worker_app_id = target_worker_app["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            get_base = params.get("get_base", False)
            if get_base is False:
                req_url = f"{self.worker_group_api_prefix}/worker-group-list/?" \
                          f"workerAppId={worker_app_id}&pageNum={page_num}"
            else:
                req_url = f"{self.zhiyoufy_addr}{self.api_base_url}/active-worker/group-base-list/?" \
                          f"workerAppId={worker_app_id}&pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            if exact_match:
                req_url += f"&exactMatch=true"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            if get_base is False:
                return call_rsp["data"]["list"]
            else:
                return call_rsp["data"]

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_group_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s worker_group_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            worker_app_var_path = params["worker_app_var_path"]
            name = params["name"]
            must_exist = params.get("must_exist", False)
            worker_group_var_path = params.get("worker_group_var_path", None)

            worker_group_list = self.worker_group_get_list({
                "worker_app_var_path": worker_app_var_path,
                "exactMatch": True,
                "keyword": name,
                "get_base": params.get("get_base", False)
            })

            target_worker_group = None

            for worker_group in worker_group_list:
                if worker_group["name"] == name:
                    target_worker_group = worker_group
                    break

            if not target_worker_group and must_exist:
                raise Exception(f"Not found job folder {name}")

            if worker_group_var_path:
                self.set_step_var(worker_group_var_path, target_worker_group)

            return target_worker_group
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_group_update(self, params):
        log_prefix = "%s %s worker_group_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_group_var_path = params.get("worker_group_var_path", None)
            timeout = params.get("timeout", 10)

            if worker_group_var_path:
                target_worker_group = self.get_step_var(worker_group_var_path)
            else:
                target_worker_group = self.worker_group_get_single_by_name(params)

            if not target_worker_group:
                raise Exception("%s not found worker_group" % log_prefix)

            req_url = f"{self.worker_group_api_prefix}/update-worker-group/{target_worker_group['id']}"

            body_info = {}

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            for key in ["name", "description"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_group_del_by_id(self, worker_group_id):
        log_prefix = "%s %s worker_group_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with worker_group_id %s" % (
            log_prefix, worker_group_id))

        try:
            req_url = f"{self.worker_group_api_prefix}/del-worker-group/{worker_group_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
