import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientEnvironment(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def environment_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/environment"

    def environment_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s environment_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        environment_var_path = params.get("environment_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if environment_var_path:
            target_environment = self.get_step_var(environment_var_path)
        else:
            target_environment = self.environment_get_single_by_name(params)

        if not target_environment:
            self.environment_create(params)
        elif update_if_exist:
            self.environment_update(params)

    def environment_create(self, params):
        log_prefix = "%s %s environment_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.environment_api_prefix}/add-environment"

            timeout = params.get("timeout", 10)

            parent_env_var_path = params.get("parent_env_var_path", None)
            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            body_info = {}

            if parent_env_var_path:
                parent_env = self.get_step_var(parent_env_var_path)
                body_info["parentId"] = parent_env["id"]
                body_info["parentName"] = parent_env["name"]

            for key in ["name", "description", "extraArgs"]:
                if key in params:
                    body_info[key] = params[key]

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_del_by_id(self, environment_id, environment_name=None):
        log_prefix = "%s %s environment_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with environment_id %s" % (
            log_prefix, environment_id))

        try:
            req_url = f"{self.environment_api_prefix}/del-environment/{environment_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            self.clear_environment_cache(environment_name)

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_del_by_name(self, params):
        log_prefix = "%s %s environment_del_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            target_environment = self.environment_get_single_by_name(params)

            if target_environment:
                self.environment_del_by_id(target_environment["id"], target_environment["name"])
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_get_list(self, params, trace_on=True):
        log_prefix = "%s %s environment_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            all_users = params.get("allUsers", None)
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.environment_api_prefix}/environment-list/?pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if all_users is not None:
                req_url += f"&allUsers={all_users}"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            for environment_to_cache in call_rsp["data"]["list"]:
                self.set_environment_cache(environment_to_cache)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_get_full_list(self, params):
        total_environment_list = []
        get_list_params = params.copy()

        page_num = 1
        while True:
            get_list_params["pageNum"] = page_num
            environment_list = self.environment_get_list(get_list_params)

            if not environment_list:
                return total_environment_list

            total_environment_list.extend(environment_list)
            page_num += 1

    def environment_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s environment_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            name = params["name"]
            must_exist = params.get("must_exist", False)
            environment_var_path = params.get("environment_var_path", None)

            target_environment = self.get_environment_cache(name)

            if target_environment:
                if trace_on:
                    self.robot_logger.info("%s found in cache with cached_environment" % (
                        log_prefix,))
            else:
                self.check_login()

                timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

                get_base = params.get("get_base", False)
                if get_base is False:
                    req_url = f"{self.environment_api_prefix}/environment-list/?keyword={name}&exactMatch=true"
                else:
                    req_url = f"{self.environment_api_prefix}/environment-base-list/?keyword={name}&exactMatch=true"

                self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

                r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

                call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

                if get_base is True:
                    environment_list = call_rsp["data"]
                else:
                    environment_list = call_rsp["data"]["list"]

                for environment_to_cache in environment_list:
                    if not target_environment and environment_to_cache["name"] == name:
                        target_environment = environment_to_cache
                    self.set_environment_cache(environment_to_cache)

            if not target_environment and must_exist:
                raise Exception(f"Not found environment {name}")

            if trace_on:
                if target_environment:
                    self.robot_logger.info("%s found target_environment \n%s" % (
                        log_prefix, StrUtil.pprint(target_environment)))
                else:
                    self.robot_logger.info("%s not found target_environment" % (
                        log_prefix,))

            if environment_var_path:
                self.set_step_var(environment_var_path, target_environment)

            return target_environment
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_update(self, params):
        log_prefix = "%s %s environment_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            environment_var_path = params.get("environment_var_path", None)

            if environment_var_path:
                target_environment = self.get_step_var(environment_var_path)
            else:
                target_environment = self.environment_get_single_by_name(params)

            if not target_environment:
                raise Exception("%s not found environment" % log_prefix)

            req_url = f"{self.environment_api_prefix}/update-environment/{target_environment['id']}"

            parent_env_var_path = params.get("parent_env_var_path", None)
            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

            if parent_env_var_path:
                parent_env = self.get_step_var(parent_env_var_path)
                body_info["parentId"] = parent_env["id"]
                body_info["parentName"] = parent_env["name"]

            for key in ["name", "description", "extraArgs"]:
                if key in params:
                    body_info[key] = params[key]

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_user_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s environment_user_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        environment_user_var_path = params.get("environment_user_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if environment_user_var_path:
            target_environment_user = self.get_step_var(environment_user_var_path)
        else:
            target_environment_user = self.environment_user_get_single_by_name(params)

        if not target_environment_user:
            self.environment_user_create(params)
        elif update_if_exist:
            self.environment_user_update(params)

    def environment_user_create(self, params):
        log_prefix = "%s %s environment_user_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.environment_api_prefix}/add-environment-user"

            environment_var_path = params["environment_var_path"]
            user_var_path = params["user_var_path"]

            target_environment = self.get_step_var(environment_var_path)
            target_user = self.get_step_var(user_var_path)

            timeout = params.get("timeout", 10)

            body_info = {
                "environmentId": target_environment["id"],
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

    def environment_user_del(self, params):
        log_prefix = "%s %s environment_user_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            environment_user_var_path = params.get("environment_user_var_path", None)

            if environment_user_var_path:
                target_environment_user = self.get_step_var(environment_user_var_path)
            else:
                target_environment_user = self.environment_user_get_single_by_name(params)

            if target_environment_user:
                self.environment_user_del_by_id(target_environment_user["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_user_get_list(self, params, trace_on=True):
        log_prefix = "%s %s environment_user_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            environment_var_path = params["environment_var_path"]

            target_environment = self.get_step_var(environment_var_path)
            environment_id = target_environment["id"]

            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.environment_api_prefix}/environment-user-list/{environment_id}?pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_user_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s environment_user_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            username = params["username"]
            must_exist = params.get("must_exist", False)
            environment_var_path = params["environment_var_path"]
            environment_user_var_path = params.get("environment_user_var_path", None)

            target_environment_user = None
            page_size = 100
            page_num = 1

            while True:
                environment_user_list = self.environment_user_get_list({
                    "environment_var_path": environment_var_path,
                    "pageNum": page_num,
                    "pageSize": page_size,
                })

                for environment_user in environment_user_list:
                    if not target_environment_user and environment_user["username"] == username:
                        target_environment_user = environment_user
                        break

                if target_environment_user:
                    break

                if len(environment_user_list) < page_size:
                    break

            if not target_environment_user and must_exist:
                raise Exception(f"Not found environment user {username}")

            if trace_on:
                if target_environment_user:
                    self.robot_logger.info("%s found target_environment_user \n%s" % (
                        log_prefix, StrUtil.pprint(target_environment_user)))
                else:
                    self.robot_logger.info("%s not found target_environment_user" % (
                        log_prefix,))

            if environment_user_var_path:
                self.set_step_var(environment_user_var_path, target_environment_user)

            return target_environment_user
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_user_sync(self, params):
        log_prefix = "%s %s environment_user_sync:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            user_var_path = params["user_var_path"]
            target_user = self.get_step_var(user_var_path)
            user_name = target_user["username"]

            cur_total_environment_list = self.environment_get_full_list(params)

            target_environment_user_config = params["environment_user_config"]
            add_only = params.get("add_only", True)

            add_to_environment = dict()
            del_from_environment = []

            for environment_name in target_environment_user_config.keys():
                for cur_environment in cur_total_environment_list:
                    if cur_environment["name"] == environment_name:
                        add_to_environment[environment_name] = target_environment_user_config[environment_name]
                        break

            if not add_only:
                for cur_environment in cur_total_environment_list:
                    found = False
                    for environment_name in target_environment_user_config.keys():
                        if cur_environment["name"] == environment_name:
                            found = True
                            break
                    if not found:
                        del_from_environment.append(cur_environment["name"])

            for environment_name, user_info in add_to_environment.items():
                self.environment_get_single_by_name({"environment_var_path": "target_environment",
                                                     "name": environment_name,
                                                     "must_exist": True
                                                     })

                environment_user_params = {
                    "environment_var_path": "target_environment",
                    "user_var_path": user_var_path,
                    "username": user_name,
                    "isOwner": user_info["isOwner"],
                    "isEditor": user_info["isEditor"]
                }

                self.environment_user_create_if_missing_update_if_requested(environment_user_params)

            for environment_name in del_from_environment:
                self.environment_get_single_by_name({"name": environment_name,
                                                     "environment_var_path": "target_environment",
                                                     "must_exist": True
                                                     })

                del_environment_user_params = {
                    "environment_var_path": "target_environment",
                    "username": user_name
                }
                self.environment_user_del(del_environment_user_params)

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def environment_user_update(self, params):
        log_prefix = "%s %s environment_user_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            environment_user_var_path = params.get("environment_user_var_path", None)

            if environment_user_var_path:
                target_environment_user = self.get_step_var(environment_user_var_path)
            else:
                target_environment_user = self.environment_user_get_single_by_name(params)

            if not target_environment_user:
                raise Exception("%s not found environment user" % log_prefix)

            req_url = f"{self.environment_api_prefix}/update-environment-user/{target_environment_user['id']}"

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

    def environment_user_del_by_id(self, environment_user_id):
        log_prefix = "%s %s environment_user_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with environment_user_del_by_id %s" % (
            log_prefix, environment_user_id))

        try:
            req_url = f"{self.environment_api_prefix}/del-environment-user/{environment_user_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
