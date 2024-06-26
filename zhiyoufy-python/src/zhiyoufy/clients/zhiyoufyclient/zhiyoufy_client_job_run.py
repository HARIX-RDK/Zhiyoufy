import json
import os

import requests
import time
from datetime import datetime, timedelta

from zhiyoufy.common.utils import CheckUtil, LogUtil, StrUtil, RandomUtil, TimeUtil

from .zhiyoufy_client_bridge import ZhiyoufyClientBridge


class ZhiyoufyClientJobRun(ZhiyoufyClientBridge):
    def __init__(self, parent_client):
        super().__init__(parent_client=parent_client)

    def job_run_create(self, params):
        log_prefix = "%s %s job_run_create:" % (self.log_prefix, self.zhiyoufy_addr)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            req_url = "%s%s/job-run/start" % (self.zhiyoufy_addr, self.api_base_url)

            timeout = params["timeout"] if "timeout" in params else 10

            parallel_num = params.get("parallelNum", 1)
            run_num = params.get("runNum", parallel_num)
            run_tag = params.get("run_tag", "EXEC_IMMED")

            if parallel_num > run_num:
                raise Exception("%s parallel_num %s is greater than run_num %s" % (
                    log_prefix, parallel_num, run_num
                ))

            project_id = params.get("projectId", None)
            project_name = params.get("projectName", None)

            if project_id is None and project_name is not None:
                project_params = {
                    "name": project_name,
                    "project_var_path": "project_var"
                }
                target_project = self.project_client.project_get_single_by_name(project_params,
                                                                                trace_on=False)
                if not target_project:
                    raise Exception("%s not found target project with name %s" % (
                        log_prefix, project_name
                    ))
                project_id = target_project["id"]

            template_id = params.get("templateId", None)
            template_name = params.get("templateName", None)

            if template_id is None and template_name is not None:
                template_params = {
                    "name": template_name,
                    "project_var_path": "project_var"
                }
                target_template = self.job_template_client.job_template_get_single_by_name(template_params,
                                                                                           trace_on=False)
                if not target_template:
                    raise Exception("%s not found target template with name %s" % (
                        log_prefix, template_name
                    ))
                template_id = target_template["id"]

            environment_id = params.get("environmentId", None)
            environment_name = params.get("environmentName", None)

            if environment_id is None and environment_name is not None:
                environment_params = {
                    "name": environment_name,
                    "get_base": True
                }
                target_environment = self.environment_client.environment_get_single_by_name(environment_params,
                                                                                            trace_on=True)
                if not target_environment:
                    raise Exception("%s not found target environment with name %s" % (
                        log_prefix, environment_name
                    ))
                environment_id = target_environment["id"]

            worker_app_id = params.get("workerAppId", None)
            worker_app_name = params.get("workerAppName", None)

            if worker_app_id is None and worker_app_name is not None:
                worker_app_params = {
                    "name": worker_app_name,
                    "worker_app_var_path": "worker_app_var",
                    "get_base": True
                }
                target_worker_app = self.worker_app_client.worker_app_get_single_by_name(worker_app_params,
                                                                                         trace_on=True)
                if not target_worker_app:
                    raise Exception("%s not found target worker app with name %s" % (
                        log_prefix, worker_app_name
                    ))
                worker_app_id = target_worker_app["id"]

            worker_group_id = params.get("workerGroupId", None)
            worker_group_name = params.get("workerGroupName", None)

            if worker_group_id is None and worker_group_name is not None:
                worker_group_params = {
                    "name": worker_group_name,
                    "get_base": True,
                    "worker_app_var_path": "worker_app_var"
                }
                target_worker_group = self.worker_group_client.worker_group_get_single_by_name(worker_group_params,
                                                                                               trace_on=False)
                if not target_worker_group:
                    raise Exception("%s not found target worker group with name %s" % (
                        log_prefix, worker_group_name
                    ))
                worker_group_id = target_worker_group["id"]

            run_guid = RandomUtil.gen_guid()
            body_info = {
                "runGuid": run_guid,
                "parallelNum": parallel_num,
                "runNum": run_num
            }

            for key in ["projectName", "templateName", "environmentName", "workerAppName",
                        "workerGroupName", "userName"]:
                if key in params:
                    body_info[key] = params[key]

            if template_id is not None:
                body_info["templateId"] = template_id
            if environment_id is not None:
                body_info["environmentId"] = environment_id
            if project_id is not None:
                body_info["projectId"] = project_id
            if worker_app_id is not None:
                body_info["workerAppId"] = worker_app_id
            if worker_group_id is not None:
                body_info["workerGroupId"] = worker_group_id

            body_info["runTag"] = run_tag

            data = json.dumps(body_info)

            self.robot_logger.info("%s req_url %s, guid %s\n, request_body: %s" % (log_prefix,
                                                                                   req_url, run_guid,
                                                                                   json.dumps(body_info, indent=4)))

            job_info_var_path = params.get("job_info_var_path", None)
            if job_info_var_path:
                self.set_step_var(job_info_var_path, body_info)

            r = requests.post(req_url, headers=self.headers, data=data,
                              timeout=timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_run_del(self, params):
        log_prefix = "%s %s job_run_del:" % (self.log_prefix, self.zhiyoufy_addr)

        self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            run_guid_var_path = params.get("run_guid_var_path", None)
            if run_guid_var_path:
                run_guid = self.get_step_var(run_guid_var_path)
            else:
                run_guid = params["run_guid"]

            req_url = "%s%s/job-run/stop/%s" % (self.zhiyoufy_addr, self.api_base_url, run_guid)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.post(req_url, headers=self.headers, timeout=self.config_inst.norm_timeout, verify=False)

            CheckUtil.validate_and_return_json_rsp(log_prefix, r)
        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_run_get_full_child_result_list(self, params, trace_on=True):
        log_prefix = "%s %s job_run_get_full_child_result_list:" % (self.log_prefix, self.zhiyoufy_addr)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        page_size = 10
        page_num = 1
        clear_file = True

        full_child_result_list = []

        try:
            while True:
                params["page_num"] = page_num
                params["page_size"] = page_size

                child_result_list = self.job_run_get_child_result_list(params, trace_on, clear_file=clear_file)
                if not child_result_list or len(child_result_list) == 0:
                    break
                full_child_result_list.extend(child_result_list)
                page_num += 1
                clear_file = False

            if trace_on:
                self.robot_logger.info("%s, full_child_result_list= %s", log_prefix, full_child_result_list)

            return full_child_result_list

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_run_get_child_result_list(self, params, trace_on=True, clear_file=False):
        log_prefix = "%s %s job_run_get_child_result_list:" % (self.log_prefix, self.zhiyoufy_addr)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            run_guid_var_path = params.get("run_guid_var_path", None)
            if run_guid_var_path:
                run_guid = self.get_step_var(run_guid_var_path)
            else:
                run_guid = params["run_guid"]

            self.check_login()

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = "%s%s/job-run/child-result-list/?runGuid=%s" % (
                self.zhiyoufy_addr, self.api_base_url, run_guid)

            template_id_var_path = params.get("template_id_var_path", None)
            if template_id_var_path:
                template_id = self.get_step_var(template_id_var_path)
            else:
                template_id = params["template_id"]

            project_id_var_path = params.get("project_id_var_path", None)
            if project_id_var_path:
                project_id = self.get_step_var(project_id_var_path)
            else:
                project_id = params["project_id"]

            environment_id_var_path = params.get("environment_id_var_path", None)
            if environment_id_var_path:
                environment_id = self.get_step_var(environment_id_var_path)
            else:
                environment_id = params["environment_id"]

            page_num = params.get("page_num", 1)
            page_size = params.get("page_size", 10)

            req_url = f"{req_url}&projectId={project_id}&templateId={template_id}" \
                      f"&environmentId={environment_id}&pageNum={page_num}&pageSize={page_size}"

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            export_file_path = params.get("export_file_path", None)
            if export_file_path is not None:
                result_ok_file_path = os.path.join(export_file_path, 'result_ok.txt')
                result_fail_file_path = os.path.join(export_file_path, 'result_fail.txt')

                if clear_file:
                    with open(result_ok_file_path, 'w') as result_ok_file:
                        result_ok_file.truncate(0)
                    with open(result_fail_file_path,'w') as result_fail_file:
                        result_fail_file.truncate(0)

                result_list_for_success = []
                result_list_for_failure = []

                if "data" in call_rsp:
                    for child_result in call_rsp["data"]["list"]:
                        if child_result["passed"] is True:
                            result_list_for_success.append(child_result)
                        else:
                            result_list_for_failure.append(child_result)

                    if result_list_for_success:
                        with open(result_ok_file_path, 'a') as result_ok_file:
                            for child_result in result_list_for_success:
                                result_ok_file.write(child_result["jobOutputUrl"] + '\n')

                    if result_list_for_failure:
                        with open(result_fail_file_path, 'a') as result_fail_file:
                            for child_result in result_list_for_failure:
                                result_fail_file.write(child_result["jobOutputUrl"] + '\n')

            return call_rsp["data"]["list"]

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_run_get_single(self, params, trace_on=True):
        log_prefix = "%s %s job_run_get_single:" % (self.log_prefix, self.zhiyoufy_addr)

        if trace_on:
            self.robot_logger.info("%s Enter with \n%s" % (log_prefix, StrUtil.pprint(params)))

        try:
            run_guid_var_path = params.get("run_guid_var_path", None)
            if run_guid_var_path:
                run_guid = self.get_step_var(run_guid_var_path)
            else:
                run_guid = params["run_guid"]

            self.check_login()

            timeout = params.get("timeout", self.config_inst.norm_timeout)

            req_url = "%s%s/job-run/result/%s" % (
                self.zhiyoufy_addr, self.api_base_url, run_guid)

            self.robot_logger.info("%s req_url %s" % (log_prefix, req_url))

            r = requests.get(req_url, headers=self.headers, timeout=timeout, verify=False)

            call_rsp = CheckUtil.validate_and_return_json_rsp(log_prefix, r, trace_on=trace_on)

            return call_rsp

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)

    def job_run_wait_job_done(self, params):
        log_prefix = "%s %s job_run_wait_job_done:" % (self.log_prefix, self.zhiyoufy_addr)

        self.robot_logger.info("%s Enter with %s" % (
            log_prefix, StrUtil.pprint(params)))

        try:

            run_guid_var_path = params.get("run_guid_var_path", None)
            if run_guid_var_path:
                run_guid = self.get_step_var(run_guid_var_path)
            else:
                run_guid = params["run_guid"]

            timeout = params.get("timeout", 3600)
            query_interval = params.get("query_interval",10)
            initial_delay = params.get("initial_delay", 10)
            stop_job_if_timeout = params.get("stop_job_if_timeout", False)

            final_time = datetime.now() + timedelta(seconds=timeout)

            first = True
            try_once_more = True

            self.robot_logger.info("%s: wait_build_done start\n" % log_prefix)
            start_time = TimeUtil.get_current_time_ms()

            while datetime.now() <= final_time or try_once_more:
                if first:
                    first = False
                    time.sleep(initial_delay - 1)
                else:
                    time.sleep(query_interval - 1)

                if datetime.now() > final_time:
                    try_once_more = False

                get_single_rsp = self.job_run_get_single(params)
                job_run_result = get_single_rsp.get("data", None)

                if job_run_result:
                    self.robot_logger.info("%s: job_run_result: %s" % (log_prefix, json.dumps(job_run_result, indent=4)))

                    durationSeconds = job_run_result["durationSeconds"]
                    if durationSeconds == 0:
                        continue

                    end_time = TimeUtil.get_current_time_ms()
                    duration = end_time - start_time

                    self.robot_logger.info("%s: job_run_wait_job_done finish ok, "
                                           "duration %s" % (log_prefix, duration))
                    return

            if stop_job_if_timeout is True:
                del_params = {
                    "run_guid": run_guid
                }
                self.job_run_del(del_params)

            err_msg = "%s: wait_job_done timeout" % (log_prefix,)
            self.robot_logger.error(err_msg)

            raise Exception(err_msg)

        except Exception as e:
            LogUtil.log_then_rethrown(log_prefix, e)
