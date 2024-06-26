import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientJobFolder(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def job_folder_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/job-folder"

    def job_folder_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s job_folder_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        job_folder_var_path = params.get("job_folder_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if job_folder_var_path:
            target_job_folder = self.get_step_var(job_folder_var_path)
        else:
            target_job_folder = self.job_folder_get_single_by_name(params)

        if not target_job_folder:
            self.job_folder_create(params)
        elif update_if_exist:
            self.job_folder_update(params)

    def job_folder_create(self, params):
        log_prefix = "%s %s job_folder_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_var_path = params["project_var_path"]
            parent_folder_var_path = params["parent_folder_var_path"]

            target_project = self.get_step_var(project_var_path)

            if parent_folder_var_path == "root":
                target_parent_folder = {"id": 0, "name": "root"}
            else:
                target_parent_folder = self.get_step_var(parent_folder_var_path)

            req_url = f"{self.job_folder_api_prefix}/add-job-folder"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "projectId": target_project["id"],
                "projectName": target_project["name"],
                "parentId": target_parent_folder["id"],
                "parentName": target_parent_folder["name"],
            }

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

    def job_folder_del(self, params):
        log_prefix = "%s %s job_folder_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            job_folder_var_path = params.get("job_folder_var_path", None)

            if job_folder_var_path:
                target_job_folder = self.get_step_var(job_folder_var_path)
            else:
                target_job_folder = self.job_folder_get_single_by_name(params)

            if target_job_folder:
                self.job_folder_del_by_id(target_job_folder["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_folder_get_list(self, params, trace_on=True):
        log_prefix = "%s %s job_folder_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            project_var_path = params["project_var_path"]
            parent_folder_var_path = params.get("parent_folder_var_path", None)
            parent_folder = None

            target_project = self.get_step_var(project_var_path)
            if parent_folder_var_path is not None:
                if parent_folder_var_path == "root":
                    parent_folder = {"id": 0}
                else:
                    parent_folder = self.get_step_var(parent_folder_var_path)

            project_id = target_project["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = f"{self.job_folder_api_prefix}/job-folder-list/?projectId={project_id}&pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if keyword is not None:
                req_url += f"&keyword={keyword}"

            if parent_folder is not None:
                parent_id = parent_folder["id"]
                req_url += f"&parentId={parent_id}"

            if exact_match:
                req_url += f"&exactMatch=true"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp["data"]["list"]
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_folder_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s job_folder_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            project_var_path = params["project_var_path"]
            parent_folder_var_path = params.get("parent_folder_var_path", None)
            name = params["name"]
            must_exist = params.get("must_exist", False)
            job_folder_var_path = params.get("job_folder_var_path", None)

            job_folder_list = self.job_folder_get_list({
                "project_var_path": project_var_path,
                "parent_folder_var_path": parent_folder_var_path,
                "exactMatch": True,
                "keyword": name,
            })

            target_job_folder = None

            for job_folder in job_folder_list:
                if job_folder["name"] == name:
                    target_job_folder = job_folder
                    break

            if not target_job_folder and must_exist:
                raise Exception(f"Not found job folder {name}")

            if job_folder_var_path:
                self.set_step_var(job_folder_var_path, target_job_folder)

            return target_job_folder
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_folder_update(self, params):
        log_prefix = "%s %s job_folder_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            job_folder_var_path = params.get("job_folder_var_path", None)
            timeout = params.get("timeout", 10)

            if job_folder_var_path:
                target_job_folder = self.get_step_var(job_folder_var_path)
            else:
                target_job_folder = self.job_folder_get_single_by_name(params)

            if not target_job_folder:
                raise Exception("%s not found job_folder" % log_prefix)

            req_url = f"{self.job_folder_api_prefix}/update-job-folder/{target_job_folder['id']}"

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

    def job_folder_del_by_id(self, job_folder_id):
        log_prefix = "%s %s job_folder_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with job_folder_id %s" % (
            log_prefix, job_folder_id))

        try:
            req_url = f"{self.job_folder_api_prefix}/del-job-folder/{job_folder_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
