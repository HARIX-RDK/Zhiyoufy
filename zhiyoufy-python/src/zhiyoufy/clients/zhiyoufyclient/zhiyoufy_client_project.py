import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientProject(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

        self.projects_cache = {}

    def clear_project_cache(self, project_name):
        if project_name is None:
            self.projects_cache.clear()
        elif project_name in self.projects_cache:
            del self.projects_cache[project_name]

    def set_project_cache(self, in_project):
        self.projects_cache[in_project["name"]] = in_project

    def get_project_cache(self, project_name):
        return self.projects_cache.get(project_name, None)
    
    @property
    def project_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/project"

    def project_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s project_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        project_var_path = params.get("project_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if project_var_path:
            target_project = self.get_step_var(project_var_path)
        else:
            target_project = self.project_get_single_by_name(params)

        if not target_project:
            self.project_create(params)
        elif update_if_exist:
            self.project_update(params)

    def project_create(self, params):
        log_prefix = "%s %s project_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.project_api_prefix}/add-project"

            timeout = params.get("timeout", 10)

            body_info = {}

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

    def project_del(self, params):
        log_prefix = "%s %s project_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_var_path = params.get("project_var_path", None)

            if project_var_path:
                target_project = self.get_step_var(project_var_path)
            else:
                target_project = self.project_get_single_by_name(params)

            if target_project:
                self.project_del_by_id(target_project["id"], target_project["name"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_get_list(self, params, trace_on=True):
        log_prefix = "%s %s project_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            all_users = params.get("allUsers", None)
            exact_match = params.get("exactMatch", False)
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.project_api_prefix}/project-list/?pageNum={page_num}"

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

            for project_to_cache in call_rsp["data"]["list"]:
                self.set_project_cache(project_to_cache)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_get_full_list(self, params):
        total_project_list = []
        get_list_params = params.copy()

        page_num = 1
        while True:
            get_list_params["pageNum"] = page_num
            project_list = self.project_get_list(get_list_params)

            if not project_list:
                return total_project_list

            total_project_list.extend(project_list)
            page_num += 1

    def project_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s project_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            name = params["name"]
            must_exist = params.get("must_exist", False)
            project_var_path = params.get("project_var_path", None)

            target_project = self.get_project_cache(name)

            if target_project:
                if trace_on:
                    self.robot_logger.info("%s found in cache with cached_project" % (
                        log_prefix,))
            else:
                project_list = self.project_get_list({
                    "exactMatch": True,
                    "keyword": name,
                })

                for project_to_cache in project_list:
                    if not target_project and project_to_cache["name"] == name:
                        target_project = project_to_cache
                    self.set_project_cache(project_to_cache)

            if not target_project and must_exist:
                raise Exception(f"Not found project {name}")

            if trace_on:
                if target_project:
                    self.robot_logger.info("%s found target_project \n%s" % (
                        log_prefix, StrUtil.pprint(target_project)))
                else:
                    self.robot_logger.info("%s not found target_project" % (
                        log_prefix,))

            if project_var_path:
                self.set_step_var(project_var_path, target_project)

            return target_project
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_update(self, params):
        log_prefix = "%s %s project_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_var_path = params.get("project_var_path", None)

            if project_var_path:
                target_project = self.get_step_var(project_var_path)
            else:
                target_project = self.project_get_single_by_name(params)

            if not target_project:
                raise Exception("%s not found project" % log_prefix)

            req_url = f"{self.project_api_prefix}/update-project/{target_project['id']}"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

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

    def project_del_by_id(self, project_id, project_name=None):
        log_prefix = "%s %s project_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with project_id %s" % (
            log_prefix, project_id))

        try:
            req_url = f"{self.project_api_prefix}/del-project/{project_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            self.clear_project_cache(project_name)

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_user_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s project_user_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        project_user_var_path = params.get("project_user_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if project_user_var_path:
            target_project_user = self.get_step_var(project_user_var_path)
        else:
            target_project_user = self.project_user_get_single_by_name(params)

        if not target_project_user:
            self.project_user_create(params)
        elif update_if_exist:
            self.project_user_update(params)

    def project_user_create(self, params):
        log_prefix = "%s %s project_user_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.project_api_prefix}/add-project-user"

            project_var_path = params["project_var_path"]
            user_var_path = params["user_var_path"]

            target_project = self.get_step_var(project_var_path)
            target_user = self.get_step_var(user_var_path)

            timeout = params.get("timeout", 10)

            body_info = {
                "projectId": target_project["id"],
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

    def project_user_del(self, params):
        log_prefix = "%s %s project_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_user_var_path = params.get("project_user_var_path", None)

            if project_user_var_path:
                target_project_user = self.get_step_var(project_user_var_path)
            else:
                target_project_user = self.project_user_get_single_by_name(params)

            if target_project_user:
                self.project_user_del_by_id(target_project_user["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_user_get_list(self, params, trace_on=True):
        log_prefix = "%s %s project_user_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            project_var_path = params["project_var_path"]

            target_project = self.get_step_var(project_var_path)
            project_id = target_project["id"]

            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.project_api_prefix}/project-user-list/{project_id}?pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_user_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s project_user_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            username = params["username"]
            must_exist = params.get("must_exist", False)
            project_var_path = params["project_var_path"]
            project_user_var_path = params.get("project_user_var_path", None)

            target_project_user = None
            page_size = 100
            page_num = 1

            while True:
                project_user_list = self.project_user_get_list({
                    "project_var_path": project_var_path,
                    "pageNum": page_num,
                    "pageSize": page_size,
                })

                for project_user in project_user_list:
                    if not target_project_user and project_user["username"] == username:
                        target_project_user = project_user
                        break

                if target_project_user:
                    break

                if len(project_user_list) < page_size:
                    break

            if not target_project_user and must_exist:
                raise Exception(f"Not found project {username}")

            if trace_on:
                if target_project_user:
                    self.robot_logger.info("%s found target_project_user \n%s" % (
                        log_prefix, StrUtil.pprint(target_project_user)))
                else:
                    self.robot_logger.info("%s not found target_project_user" % (
                        log_prefix,))

            if project_user_var_path:
                self.set_step_var(project_user_var_path, target_project_user)

            return target_project_user
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_user_sync(self, params):
        log_prefix = "%s %s project_user_sync:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            user_var_path = params["user_var_path"]
            target_user = self.get_step_var(user_var_path)
            user_name = target_user["username"]

            cur_total_project_list = self.project_get_full_list(params)

            target_project_user_config = params["project_user_config"]
            add_only = params.get("add_only", True)

            add_to_project = dict()
            del_from_project = []

            for project_name in target_project_user_config.keys():
                for cur_project in cur_total_project_list:
                    if cur_project["name"] == project_name:
                        add_to_project[project_name] = target_project_user_config[project_name]
                        break

            if not add_only:
                for cur_project in cur_total_project_list:
                    found = False
                    for project_name in target_project_user_config.keys():
                        if cur_project["name"] == project_name:
                            found = True
                            break
                    if not found:
                        del_from_project.append(cur_project["name"])

            for project_name, user_info in add_to_project.items():
                self.project_get_single_by_name({"project_var_path": "target_project",
                                                     "name": project_name,
                                                     "must_exist": True
                                                     })

                project_user_params = {
                    "project_var_path": "target_project",
                    "user_var_path": user_var_path,
                    "username": user_name,
                    "isOwner": user_info["isOwner"],
                    "isEditor": user_info["isEditor"]
                }

                self.project_user_create_if_missing_update_if_requested(project_user_params)

            for project_name in del_from_project:
                self.project_get_single_by_name({"name": project_name,
                                                     "project_var_path": "target_project",
                                                     "must_exist": True
                                                     })

                del_project_user_params = {
                    "project_var_path": "target_project",
                    "username": user_name
                }
                self.project_user_del(del_project_user_params)

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def project_user_update(self, params):
        log_prefix = "%s %s project_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_user_var_path = params.get("project_user_var_path", None)

            if project_user_var_path:
                target_project_user = self.get_step_var(project_user_var_path)
            else:
                target_project_user = self.project_user_get_single_by_name(params)

            if not target_project_user:
                raise Exception("%s not found project user" % log_prefix)

            req_url = f"{self.project_api_prefix}/update-project-user/{target_project_user['id']}"

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

    def project_user_del_by_id(self, project_user_id):
        log_prefix = "%s %s project_user_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with project_user_id %s" % (
            log_prefix, project_user_id))

        try:
            req_url = f"{self.project_api_prefix}/del-project-user/{project_user_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
