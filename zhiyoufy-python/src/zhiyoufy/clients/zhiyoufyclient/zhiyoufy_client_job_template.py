import copy
import json
import requests

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientJobTemplate(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    @property
    def job_template_api_prefix(self):
        return f"{self.zhiyoufy_addr}{self.api_base_url}/job-template"

    def job_template_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s job_template_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        job_template_var_path = params.get("job_template_var_path", None)
        update_if_exist = params.get("update_if_exist", True)

        if job_template_var_path:
            target_job_template = self.get_step_var(job_template_var_path)
        else:
            target_job_template = self.job_template_get_single_by_name(params)

        if not target_job_template:
            self.job_template_create(params)
        elif update_if_exist:
            self.job_template_update(params)

    def job_template_multi_create_if_missing_update_if_requested(self, params):
        log_prefix = "%s %s job_template_multi_create_if_missing_update_if_requested:" % (
            self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        copied_all = copy.copy(params)
        multi_configs = copied_all.pop("multi_configs")

        for config in multi_configs:
            copied_item = copy.copy(copied_all)
            copied_item["name"] = config["name"]
            copied_item["jobPath"] = config["jobPath"]

            for key in ["configSingles", "configCollections", "update_if_exist",
                        "job_template_var_path", "timeoutSeconds",
                        "description", "extraArgs",
                        "isPerf", "dashboardAddr"]:
                if key in config:
                    copied_item[key] = config[key]

            self.job_template_create_if_missing_update_if_requested(copied_item)

    def job_template_create(self, params):
        log_prefix = "%s %s job_template_create:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            project_var_path = params["project_var_path"]

            target_project = self.get_step_var(project_var_path)

            job_folder_var_path = params["job_folder_var_path"]

            target_folder = self.get_step_var(job_folder_var_path)

            req_url = f"{self.job_template_api_prefix}/add-job-template"

            timeout = params["timeout"] if "timeout" in params else 10

            body_info = {
                "projectId": target_project["id"],
                "projectName": target_project["name"],
                "folderId": target_folder["id"],
                "folderName": target_folder["name"],
            }

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            if "configSingles" in params:
                config_singles = params.get("configSingles")

                if config_singles and not isinstance(config_singles, str):
                    config_singles = ", ".join(config_singles)

                if config_singles:
                    body_info["configSingles"] = config_singles
                else:
                    body_info["configSingles"] = ""

            if "configCollections" in params:
                config_collections = params.get("configCollections", None)

                if config_collections and not isinstance(config_collections, str):
                    config_collections = ", ".join(config_collections)

                if config_collections:
                    body_info["configCollections"] = config_collections
                else:
                    body_info["configCollections"] = ""

            if "extraArgs" in params:
                extra_args = params.get("extraArgs", None)

                if extra_args and isinstance(extra_args, list):
                    extra_args = ' '.join(extra_args)

                if extra_args:
                    body_info["extraArgs"] = extra_args
                else:
                    body_info["extraArgs"] = ""

            if "description" in params:
                description = params.get("description", None)

                if description and isinstance(description, list):
                    description = ''.join(description)

                if description:
                    body_info["description"] = description
                else:
                    body_info["description"] = ""

            for key in ["name", "jobPath", "timeoutSeconds",
                        "baseConfPath", "privateConfPath",
                        "isPerf", "dashboardAddr"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_template_del(self, params):
        log_prefix = "%s %s job_template_del:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            job_template_var_path = params.get("job_template_var_path", None)

            if job_template_var_path:
                target_job_template = self.get_step_var(job_template_var_path)
            else:
                target_job_template = self.job_template_get_single_by_name(params)

            if target_job_template:
                self.job_template_del_by_id(target_job_template["id"])
            else:
                self.robot_logger.info("%s not found" % (log_prefix,))
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_template_get_list(self, params, trace_on=True):
        log_prefix = "%s %s job_template_get_list:" % (self.log_prefix, self.zhiyoufy_addr_base)

        try:
            self.check_login()

            project_var_path = params["project_var_path"]
            job_folder_var_path = params.get("job_folder_var_path", None)

            target_project = self.get_step_var(project_var_path)
            target_job_folder = self.get_step_var(job_folder_var_path) if job_folder_var_path else None

            project_id = target_project["id"]
            keyword = params.get("keyword", None)
            page_size = params.get("pageSize", None)
            page_num = params.get("pageNum", 1)
            exact_match = params.get("exactMatch", False)

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = f"{self.job_template_api_prefix}/job-template-list/?projectId={project_id}&pageNum={page_num}"

            if page_size is not None:
                req_url += f"&pageSize={page_size}"

            if target_job_folder is not None:
                folder_id = target_job_folder["id"]
                req_url += f"&folderId={folder_id}"

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

    def job_template_get_single_by_name(self, params, trace_on=True):
        log_prefix = "%s %s job_template_get_single_by_name:" % (self.log_prefix, self.zhiyoufy_addr_base)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            self.check_login()

            project_var_path = params["project_var_path"]
            job_folder_var_path = params.get("job_folder_var_path", None)
            name = params["name"]
            must_exist = params.get("must_exist", False)
            job_template_var_path = params.get("job_template_var_path", None)

            job_template_list = self.job_template_get_list({
                "project_var_path": project_var_path,
                "job_folder_var_path": job_folder_var_path,
                "exactMatch": True,
                "keyword": name,
            })

            target_job_template = None

            for job_template in job_template_list:
                if job_template["name"] == name:
                    target_job_template = job_template
                    break

            if not target_job_template and must_exist:
                raise Exception(f"Not found job template {name}")

            if job_template_var_path:
                self.set_step_var(job_template_var_path, target_job_template)

            return target_job_template
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_template_update(self, params):
        log_prefix = "%s %s job_template_update:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            job_template_var_path = params.get("job_template_var_path", None)
            timeout = params.get("timeout", 10)

            if job_template_var_path:
                target_job_template = self.get_step_var(job_template_var_path)
            else:
                target_job_template = self.job_template_get_single_by_name(params)

            if not target_job_template:
                raise Exception("%s not found job_template" % log_prefix)

            req_url = f"{self.job_template_api_prefix}/update-job-template/{target_job_template['id']}"

            body_info = {}

            worker_labels = params.get("workerLabels", None)

            if worker_labels and not isinstance(worker_labels, str):
                worker_labels = json.dumps(worker_labels)

            if worker_labels:
                body_info["workerLabels"] = worker_labels

            if "configSingles" in params:
                config_singles = params.get("configSingles")

                if config_singles and not isinstance(config_singles, str):
                    config_singles = ", ".join(config_singles)

                if config_singles:
                    body_info["configSingles"] = config_singles
                else:
                    body_info["configSingles"] = ""

            if "configCollections" in params:
                config_collections = params.get("configCollections")

                if config_collections and not isinstance(config_collections, str):
                    config_collections = ", ".join(config_collections)

                if config_collections:
                    body_info["configCollections"] = config_collections
                else:
                    body_info["configCollections"] = ""

            if "extraArgs" in params:
                extra_args = params.get("extraArgs")

                if extra_args and isinstance(extra_args, list):
                    extra_args = ' '.join(extra_args)

                if extra_args:
                    body_info["extraArgs"] = extra_args
                else:
                    body_info["extraArgs"] = ""

            if "description" in params:
                description = params.get("description")

                if description and isinstance(description, list):
                    description = ''.join(description)

                if description:
                    body_info["description"] = description
                else:
                    body_info["description"] = ""

            for key in ["name", "jobPath", "timeoutSeconds",
                        "baseConfPath", "privateConfPath"]:
                if key in params:
                    body_info[key] = params[key]

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_template_del_by_id(self, job_template_id):
        log_prefix = "%s %s job_template_del_by_id:" % (self.log_prefix, self.zhiyoufy_addr_base)

        self.robot_logger.info("%s Enter with job_template_id %s" % (
            log_prefix, job_template_id))

        try:
            req_url = f"{self.job_template_api_prefix}/del-job-template/{job_template_id}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.delete(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
