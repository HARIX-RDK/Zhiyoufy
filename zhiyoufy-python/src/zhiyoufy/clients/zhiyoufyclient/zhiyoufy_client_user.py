import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientUser(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def user_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/user"

    # region User
    def user_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s user_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        user_var_path = params.get("user_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if user_var_path:
            target_user = self.get_step_var(user_var_path)
        else:
            target_user = self.user_get_single_by_name(params)

        if not target_user:
            self.user_create(params)
        elif update_if_exist:
            self.user_update(params)

    def user_create(self, params):
        log_prefix = "%s %s user_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = f"{self.user_api_prefix}/add-user"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "username": params["username"],
                "password": params["password"],
                "email": params["email"],
            }
            for key in ["note", "enabled", "sysAdmin", "admin"]:
                if key in params:
                    body_info[key] = params[key]
            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_del_by_id(self, user_id):
        log_prefix = "%s %s user_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with user_id %s" % (
            log_prefix, user_id))

        try:
            req_url = f"{self.user_api_prefix}/del-user/{user_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            self.clear_user_cache()

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_del_by_name(self, params):
        log_prefix = "%s %s user_del_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            target_user = self.user_get_single_by_name(params)

            if target_user:
                self.user_del_by_id(target_user["id"])
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_get_list(self, params, trace_on=True):
        log_prefix = "%s %s user_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            keyword = params.get("keyword", None)
            page_num = params.get("pageNum", None)
            page_size = params.get("pageSize", None)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.user_api_prefix}/user-list"

            first_arg = True

            for var_obj, var_name in [(keyword, "keyword"), (page_num, "pageNum"), (page_size, "pageSize")]:
                if var_obj is not None:
                    if first_arg:
                        first_arg = False
                        req_url += "?"
                    else:
                        req_url += "&"
                    req_url += "%s=%s" % (var_name, var_obj)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            for user_to_cache in call_rsp["data"]["list"]:
                self.set_user_cache(user_to_cache)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s user_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            user_var_path = params.get("user_var_path", None)
            username = params["username"]
            must_exist = params.get("must_exist", False)

            for trial in range(2):
                cached_user = self.get_user_cache(username)
                if cached_user:
                    if user_var_path:
                        self.set_step_var(user_var_path, cached_user)
                    if trace_on:
                        self.robot_logger.info("%s Leave with cached_user \n%s" % (
                            log_prefix, StrUtil.pprint(cached_user)))
                    return cached_user

                if trial == 0:
                    self.user_get_list({
                        "keyword": username,
                        "pageSize": 100,
                    })

            if trace_on:
                self.robot_logger.info("%s not found user %s" % (log_prefix, username))

            if must_exist:
                raise Exception("%s not found user %s" % (log_prefix, username))

            return None
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_update(self, params):
        log_prefix = "%s %s user_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            user_var_path = params.get("user_var_path", None)

            if user_var_path:
                target_user = self.get_step_var(user_var_path)
            else:
                target_user = self.user_get_single_by_name(params)

            if not target_user:
                raise Exception("%s not found user" % log_prefix)

            self.clear_user_cache(target_user["username"])

            req_url = f"{self.user_api_prefix}/update-user/{target_user['id']}"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

            for key in ["username", "password", "email", "note", "enabled", "sysadmin", "admin"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
    # endregion

    # region Role
    def role_get_list(self, params, trace_on=True):
        log_prefix = "%s %s role_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            keyword = params.get("keyword", None)
            page_num = params.get("pageNum", None)
            page_size = params.get("pageSize", None)

            self.check_login()

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.user_api_prefix}/role-list"

            first_arg = True

            for var_obj, var_name in [(keyword, "keyword"), (page_num, "pageNum"), (page_size, "pageSize")]:
                if var_obj is not None:
                    if first_arg:
                        first_arg = False
                        req_url += "?"
                    else:
                        req_url += "&"
                    req_url += "%s=%s" % (var_name, var_obj)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            for role_to_cache in call_rsp["data"]["list"]:
                self.set_role_cache(role_to_cache)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def role_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s role_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            role_name = params["role_name"]
            must_exist = params.get("must_exist", False)
            cache_only = params.get("cache_only", True)

            for trial in range(2):
                cached_role = self.get_role_cache(role_name)
                if cached_role:
                    if trace_on:
                        self.robot_logger.info("%s Leave with cached_role \n%s" % (
                            log_prefix, StrUtil.pprint(cached_role)))
                    return cached_role

                if cache_only:
                    break

                if trial == 0:
                    self.role_get_list({
                        "keyword": role_name,
                        "pageSize": 100,
                    })

            if trace_on:
                self.robot_logger.info("%s not found role %s" % (log_prefix, role_name))

            if must_exist:
                raise Exception("%s not found role %s" % (log_prefix, role_name))

            return None
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
    # endregion

    # region User Role
    def user_role_get_list(self, params, trace_on=True):
        log_prefix = "%s %s user_role_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            user_var_path = params["user_var_path"]

            target_user = self.get_step_var(user_var_path)

            timeout = params["timeout"] if "timeout" in params else self.config_inst.norm_timeout

            req_url = f"{self.user_api_prefix}/user-role-list/{target_user['id']}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_role_list_sync(self, params):
        log_prefix = "%s %s user_role_list_sync:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            user_var_path = params["user_var_path"]

            cur_role_list = self.user_role_get_list(params)

            target_role_names = params["role_names"]
            add_only = params.get("add_only", True)

            add_role_names = []
            del_role_names = []

            for role_name in target_role_names:
                found = False
                for cur_role in cur_role_list:
                    if cur_role["name"] == role_name:
                        found = True
                        break
                if not found:
                    add_role_names.append(role_name)

            if not add_only:
                for cur_role in cur_role_list:
                    found = False
                    for role_name in target_role_names:
                        if cur_role["name"] == role_name:
                            found = True
                            break
                    if not found:
                        del_role_names.append(cur_role["name"])

            self.user_role_list_update({
                "user_var_path": user_var_path,
                "add_role_names": add_role_names,
                "del_role_names": del_role_names,
            })
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def user_role_list_update(self, params):
        log_prefix = "%s %s user_role_list_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            user_var_path = params["user_var_path"]
            add_role_names = params.get("add_role_names", [])
            del_role_names = params.get("del_role_names", [])

            if not add_role_names and not del_role_names:
                return

            target_user = self.get_step_var(user_var_path)

            req_url = f"{self.user_api_prefix}/update-user-role-list/{target_user['id']}"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {}

            if add_role_names:
                add_role_list = []
                for role_name in add_role_names:
                    target_role = self.role_get_single_by_name({
                        "role_name": role_name,
                        "must_exist": True,
                    })
                    add_role_list.append({
                        "id": target_role["id"],
                        "name": target_role["name"],
                    })
                body_info["addRoles"] = add_role_list

            if del_role_names:
                del_role_list = []
                for role_name in del_role_names:
                    target_role = self.role_get_single_by_name({
                        "role_name": role_name,
                        "must_exist": True,
                    })
                    del_role_list.append({
                        "id": target_role["id"],
                        "name": target_role["name"],
                    })
                body_info["delRoles"] = del_role_list

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s\n data %s" % (log_prefix, req_url, StrUtil.pprint(body_info)))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
    # endregion
