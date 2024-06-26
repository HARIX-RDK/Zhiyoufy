import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientWorkerApp(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def worker_app_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/worker-app"

    def worker_app_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s worker_app_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        worker_app_var_path = params.get("worker_app_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if worker_app_var_path:
            target_worker_app = self.get_step_var(worker_app_var_path)
        else:
            target_worker_app = self.worker_app_get_single_by_name(params)

        if not target_worker_app:
            self.worker_app_create(params)
        elif update_if_exist:
            self.worker_app_update(params)

    def worker_app_create(self, params):
        log_prefix = "%s %s worker_app_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.worker_app_api_prefix}/add-worker-app"

            timeout = params.get("timeout", 10)

            body_info = {}

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            for key in ["name", "description", "needConfigBeJson"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_del(self, params):
        log_prefix = "%s %s worker_app_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_var_path = params.get("worker_app_var_path", None)

            if worker_app_var_path:
                target_worker_app = self.get_step_var(worker_app_var_path)
            else:
                target_worker_app = self.worker_app_get_single_by_name(params)

            if target_worker_app:
                self.worker_app_del_by_id(target_worker_app["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_get_full_list(self, params):
        total_worker_app_list = []
        get_list_params = params.copy()

        page_num = 1
        while True:
            get_list_params["pageNum"] = page_num
            worker_app_list = self.worker_app_get_list(get_list_params)
            if not worker_app_list:
                return total_worker_app_list

            total_worker_app_list.extend(worker_app_list)
            page_num += 1

    def worker_app_get_list(self, params, trace_on=True):
        log_prefix = "%s %s worker_app_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            all_users = params.get("allUsers", None)
            exact_match = params.get("exactMatch", False)
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            get_base = params.get("get_base", False)
            if get_base is False:
                req_url = f"{self.worker_app_api_prefix}/worker-app-list/?pageNum={page_num}"
            else:
                req_url = f"{self.worker_app_api_prefix}/worker-app-base-list/?pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if all_users is not None:
                req_url += f"&allUsers={all_users}"

            if exact_match:
                req_url += f"&exactMatch=true"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            if get_base is False:
                return call_rsp["data"]["list"]
            else:
                return call_rsp["data"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s worker_app_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            name = params["name"]
            must_exist = params.get("must_exist", False)
            worker_app_var_path = params.get("worker_app_var_path", None)

            target_worker_app = None

            worker_app_list = self.worker_app_get_list({
                "exactMatch": True,
                "keyword": name,
                "get_base": params.get("get_base", False)
            })

            for worker_app in worker_app_list:
                if worker_app["name"] == name:
                    target_worker_app = worker_app
                    break

            if not target_worker_app and must_exist:
                raise Exception(f"Not found worker_app {name}")

            if trace_on:
                if target_worker_app:
                    self.robot_logger.info("%s found target_worker_app \n%s" % (
                        log_prefix, StrUtil.pprint(target_worker_app)))
                else:
                    self.robot_logger.info("%s not found target_worker_app" % (
                        log_prefix,))

            if worker_app_var_path:
                self.set_step_var(worker_app_var_path, target_worker_app)

            return target_worker_app
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_update(self, params):
        log_prefix = "%s %s worker_app_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_var_path = params.get("worker_app_var_path", None)

            if worker_app_var_path:
                target_worker_app = self.get_step_var(worker_app_var_path)
            else:
                target_worker_app = self.worker_app_get_single_by_name(params)

            if not target_worker_app:
                raise Exception("%s not found worker_app" % log_prefix)

            req_url = f"{self.worker_app_api_prefix}/update-worker-app/{target_worker_app['id']}"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            for key in ["name", "description", "needConfigBeJson"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_del_by_id(self, worker_app_id):
        log_prefix = "%s %s worker_app_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with worker_app_id %s" % (
            log_prefix, worker_app_id))

        try:
            req_url = f"{self.worker_app_api_prefix}/del-worker-app/{worker_app_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s worker_app_user_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        worker_app_user_var_path = params.get("worker_app_user_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if worker_app_user_var_path:
            target_worker_app_user = self.get_step_var(worker_app_user_var_path)
        else:
            target_worker_app_user = self.worker_app_user_get_single_by_name(params)

        if not target_worker_app_user:
            self.worker_app_user_create(params)
        elif update_if_exist:
            self.worker_app_user_update(params)

    def worker_app_user_create(self, params):
        log_prefix = "%s %s worker_app_user_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.worker_app_api_prefix}/add-worker-app-user"

            worker_app_var_path = params["worker_app_var_path"]
            user_var_path = params["user_var_path"]

            target_worker_app = self.get_step_var(worker_app_var_path)
            target_user = self.get_step_var(user_var_path)

            timeout = params.get("timeout", 10)

            body_info = {
                "workerAppId": target_worker_app["id"],
                "userId": target_user["id"],
                "username": target_user["username"],
            }

            for key in ["isOwner", "isEditor"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_del(self, params):
        log_prefix = "%s %s worker_app_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_user_var_path = params.get("worker_app_user_var_path", None)

            if worker_app_user_var_path:
                target_worker_app_user = self.get_step_var(worker_app_user_var_path)
            else:
                target_worker_app_user = self.worker_app_user_get_single_by_name(params)

            if target_worker_app_user:
                self.worker_app_user_del_by_id(target_worker_app_user["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_get_list(self, params, trace_on=True):
        log_prefix = "%s %s worker_app_user_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            worker_app_var_path = params["worker_app_var_path"]

            target_worker_app = self.get_step_var(worker_app_var_path)
            worker_app_id = target_worker_app["id"]

            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.worker_app_api_prefix}/worker-app-user-list/{worker_app_id}?pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s worker_app_user_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            username = params["username"]
            must_exist = params.get("must_exist", False)
            worker_app_var_path = params["worker_app_var_path"]
            worker_app_user_var_path = params.get("worker_app_user_var_path", None)

            target_worker_app_user = None
            page_size = 100
            page_num = 1

            while True:
                worker_app_user_list = self.worker_app_user_get_list({
                    "worker_app_var_path": worker_app_var_path,
                    "pageNum": page_num,
                    "pageSize": page_size,
                })

                for worker_app_user in worker_app_user_list:
                    if not target_worker_app_user and worker_app_user["username"] == username:
                        target_worker_app_user = worker_app_user
                        break

                if target_worker_app_user:
                    break

                if len(worker_app_user_list) < page_size:
                    break

            if not target_worker_app_user and must_exist:
                raise Exception(f"Not found worker_app {username}")

            if trace_on:
                if target_worker_app_user:
                    self.robot_logger.info("%s found target_worker_app_user \n%s" % (
                        log_prefix, StrUtil.pprint(target_worker_app_user)))
                else:
                    self.robot_logger.info("%s not found target_worker_app_user" % (
                        log_prefix,))

            if worker_app_user_var_path:
                self.set_step_var(worker_app_user_var_path, target_worker_app_user)

            return target_worker_app_user
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_sync(self, params):
        log_prefix = "%s %s worker_app_user_sync:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            user_var_path = params["user_var_path"]
            target_user = self.get_step_var(user_var_path)
            user_name = target_user["username"]

            cur_total_worker_app_list = self.worker_app_get_full_list(params)

            target_worker_app_user_config = params["worker_app_user_config"]
            add_only = params.get("add_only", True)

            add_to_worker_app = dict()
            del_from_worker_app = []

            for worker_app_name in target_worker_app_user_config.keys():
                for cur_worker_app in cur_total_worker_app_list:
                    if cur_worker_app["name"] == worker_app_name:
                        add_to_worker_app[worker_app_name] = target_worker_app_user_config[worker_app_name]
                        break

            if not add_only:
                for cur_worker_app in cur_total_worker_app_list:
                    found = False
                    for worker_app_name in target_worker_app_user_config.keys():
                        if cur_worker_app["name"] == worker_app_name:
                            found = True
                            break
                    if not found:
                        del_from_worker_app.append(cur_worker_app["name"])

            for worker_app_name, user_info in add_to_worker_app.items():
                self.worker_app_get_single_by_name({"worker_app_var_path": "target_worker_app",
                                                     "name": worker_app_name,
                                                     "must_exist": True
                                                     })

                worker_app_user_params = {
                    "worker_app_var_path": "target_worker_app",
                    "user_var_path": user_var_path,
                    "username": user_name,
                    "isOwner": user_info["isOwner"],
                    "isEditor": user_info["isEditor"]
                }

                self.worker_app_user_create_if_missing_update_if_requested(worker_app_user_params)

            for worker_app_name in del_from_worker_app:
                self.worker_app_get_single_by_name({"name": worker_app_name,
                                                     "worker_app_var_path": "target_worker_app",
                                                     "must_exist": True
                                                     })

                del_worker_app_user_params = {
                    "worker_app_var_path": "target_worker_app",
                    "username": user_name
                }
                self.worker_app_user_del(del_worker_app_user_params)

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_update(self, params):
        log_prefix = "%s %s worker_app_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            worker_app_user_var_path = params.get("worker_app_user_var_path", None)

            if worker_app_user_var_path:
                target_worker_app_user = self.get_step_var(worker_app_user_var_path)
            else:
                target_worker_app_user = self.worker_app_user_get_single_by_name(params)

            if not target_worker_app_user:
                raise Exception("%s not found worker_app user" % log_prefix)

            req_url = f"{self.worker_app_api_prefix}/update-worker-app-user/{target_worker_app_user['id']}"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

            for key in ["isOwner", "isEditor"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def worker_app_user_del_by_id(self, worker_app_user_id):
        log_prefix = "%s %s worker_app_user_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with worker_app_user_id %s" % (
            log_prefix, worker_app_user_id))

        try:
            req_url = f"{self.worker_app_api_prefix}/del-worker-app-user/{worker_app_user_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

